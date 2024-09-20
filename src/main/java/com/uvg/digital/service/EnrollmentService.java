package com.uvg.digital.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uvg.digital.entity.Enrollment;
import com.uvg.digital.entity.User;
import com.uvg.digital.entity.Course;
import com.uvg.digital.entity.Event;
import com.uvg.digital.repository.EnrollmentRepository;
import com.uvg.digital.repository.UserRepository;
import com.uvg.digital.repository.CourseRepository;
import com.uvg.digital.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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

			Enrollment enrollment = new Enrollment(null, user, course, null, "pending", LocalDateTime.now(), null);
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

			Enrollment enrollment = new Enrollment(null, user, null, event, "pending", LocalDateTime.now(), null);
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
}