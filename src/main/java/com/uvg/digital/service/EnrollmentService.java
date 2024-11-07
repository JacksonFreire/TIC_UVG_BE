package com.uvg.digital.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uvg.digital.entity.Course;
import com.uvg.digital.entity.Enrollment;
import com.uvg.digital.entity.Event;
import com.uvg.digital.entity.User;
import com.uvg.digital.model.UserEnrollmentDTO;
import com.uvg.digital.repository.CourseRepository;
import com.uvg.digital.repository.EnrollmentRepository;
import com.uvg.digital.repository.EventRepository;
import com.uvg.digital.repository.UserRepository;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void enrollUserToCourse(Long userId, Long courseId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new NoSuchElementException("Curso no encontrado"));

            if (enrollmentRepository.existsByUserAndCourse(user, course)) {
                throw new IllegalStateException("El usuario ya está inscrito en este curso");
            }

            Enrollment enrollment = new Enrollment(null, user, course, null, "pending", LocalDateTime.now(), null, null);
            enrollmentRepository.save(enrollment);

            notificationService.sendEnrollmentConfirmation(user, course, null);
            notificationService.notifyAdminOfNewEnrollment(course, null, user);
        } catch (NoSuchElementException | IllegalStateException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Transactional
    public void enrollUserToEvent(Long userId, Long eventId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NoSuchElementException("Evento no encontrado"));

            if (enrollmentRepository.existsByUserAndEvent(user, event)) {
                throw new IllegalStateException("El usuario ya está inscrito en este evento");
            }

            Enrollment enrollment = new Enrollment(null, user, null, event, "pending", LocalDateTime.now(), null, null);
            enrollmentRepository.save(enrollment);

            notificationService.sendEnrollmentConfirmation(user, null, event);
            notificationService.notifyAdminOfNewEnrollment(null, event, user);
        } catch (NoSuchElementException | IllegalStateException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public boolean isUserEnrolledInCourse(Long courseId, Long userId) {
        return enrollmentRepository.existsByCourseIdAndUserId(courseId, userId);
    }

    // Verificar si el usuario está inscrito en un evento
    public boolean isUserEnrolledInEvent(Long eventId, Long userId) {
        return enrollmentRepository.existsByEventIdAndUserId(eventId, userId);
    }

    public List<UserEnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        return enrollments.stream().map(enrollment -> {
            User user = enrollment.getUser();
            UserEnrollmentDTO dto = new UserEnrollmentDTO();
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setBirthDate(user.getBirthDate().toString());
            dto.setStatus(enrollment.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<UserEnrollmentDTO> getOrFilterEnrollmentsByCourse(Long courseId, String status) {
        List<Enrollment> enrollments;

        // Filtrar por estado si se proporciona
        if (status != null && !status.isEmpty()) {
            enrollments = enrollmentRepository.findByCourseIdAndStatus(courseId, status);
        } else {
            enrollments = enrollmentRepository.findByCourseId(courseId);
        }

        // Mapeo a UserEnrollmentDTO
        return enrollments.stream().map(enrollment -> {
            User user = enrollment.getUser();
            UserEnrollmentDTO dto = new UserEnrollmentDTO();
            dto.setUserId(user.getId());  // Setear el userId en el DTO
            dto.setCourseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null); // Setear el courseId si está disponible
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setBirthDate(user.getBirthDate().toString());
            dto.setStatus(enrollment.getStatus());
            dto.setComments(enrollment.getComments());
            return dto;
        }).collect(Collectors.toList());
    }


    public List<UserEnrollmentDTO> getEnrollmentsByEvent(Long eventId, String status) {
        List<Enrollment> enrollments;

        if (status != null && !status.isEmpty()) {
            enrollments = enrollmentRepository.findByEventIdAndStatus(eventId, status);
        } else {
            enrollments = enrollmentRepository.findByEventId(eventId);
        }

        // Mapeo a UserEnrollmentDTO para asegurar que todos los atributos están presentes
        return enrollments.stream().map(enrollment -> {
            User user = enrollment.getUser();
            UserEnrollmentDTO dto = new UserEnrollmentDTO();
            dto.setUserId(user.getId());  // Agregar el userId para futuras referencias
            dto.setEventId(enrollment.getEvent() != null ? enrollment.getEvent().getId() : null); // Setear el eventId si está disponible
            dto.setCourseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null); // Setear el courseId si está disponible
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setBirthDate(user.getBirthDate().toString());
            dto.setStatus(enrollment.getStatus());
            dto.setComments(enrollment.getComments()); // Incluir los comentarios para ser actualizados desde el cliente
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateEnrollment(UserEnrollmentDTO enrollmentDTO) {
        try {
            Enrollment enrollment;

            // Buscar la inscripción usando el ID de curso o evento
            if (enrollmentDTO.getCourseId() != null) {
                enrollment = enrollmentRepository.findByUserIdAndCourseId(enrollmentDTO.getUserId(), enrollmentDTO.getCourseId())
                        .orElseThrow(() -> new NoSuchElementException("Inscripción para el curso no encontrada"));
            } else if (enrollmentDTO.getEventId() != null) {
                enrollment = enrollmentRepository.findByUserIdAndEventId(enrollmentDTO.getUserId(), enrollmentDTO.getEventId())
                        .orElseThrow(() -> new NoSuchElementException("Inscripción para el evento no encontrada"));
            } else {
                throw new IllegalArgumentException("Debe proporcionar un courseId o eventId para actualizar la inscripción");
            }

            // Guardar los valores actuales de status y comments para compararlos luego
            String oldStatus = enrollment.getStatus();
            String oldComments = enrollment.getComments();

            // Actualizar el estado y los comentarios de la inscripción
            enrollment.setStatus(enrollmentDTO.getStatus());
            enrollment.setComments(enrollmentDTO.getComments());

            // Guardar los cambios en la inscripción
            enrollmentRepository.save(enrollment);

            // Verificar si hubo cambios en el estado o comentarios para enviar notificación
            boolean statusChanged = oldStatus == null || !oldStatus.equals(enrollment.getStatus());
            boolean commentsChanged = oldComments == null || !oldComments.equals(enrollment.getComments());

            if (statusChanged || commentsChanged) {
                // Obtener los valores actualizados para el mensaje de notificación
                User user = enrollment.getUser();
                String status = enrollment.getStatus();
                String comments = enrollment.getComments();

                // Llamar a NotificationService para enviar el correo al usuario con el indicador statusChanged
                if (enrollment.getCourse() != null) {
                    notificationService.sendUpdateNotification(user, enrollment.getCourse(), null, status, comments, statusChanged);
                } else if (enrollment.getEvent() != null) {
                    notificationService.sendUpdateNotification(user, null, enrollment.getEvent(), status, comments, statusChanged);
                } else {
                    // Manejo en caso de que no haya curso ni evento, aunque esto no debería ocurrir en un flujo normal
                    throw new IllegalArgumentException("La inscripción debe estar asociada a un curso o evento.");
                }
            }

        } catch (NoSuchElementException | IllegalArgumentException ex) {
            throw new RuntimeException("Error al actualizar la inscripción: " + ex.getMessage(), ex);
        }
    }
}