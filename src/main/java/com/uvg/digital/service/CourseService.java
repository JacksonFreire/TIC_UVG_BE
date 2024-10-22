package com.uvg.digital.service;

import com.uvg.digital.entity.Course;
import com.uvg.digital.entity.Curriculum;
import com.uvg.digital.entity.Instructor;
import com.uvg.digital.model.CourseDTO;
import com.uvg.digital.model.CurriculumDTO;
import com.uvg.digital.model.InstructorDTO;
import com.uvg.digital.repository.CourseRepository;
import com.uvg.digital.repository.CurriculumRepository;
import com.uvg.digital.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    @Autowired
    private final CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CurriculumRepository curriculumRepository;  

    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setCategory(courseDTO.getCategory());
        course.setLessonsCount(courseDTO.getLessonsCount());
        course.setStudentsCount(courseDTO.getStudentsCount());
        course.setPrice(courseDTO.getPrice());
        course.setDuration(courseDTO.getDuration());
        course.setLevel(courseDTO.getLevel());
        course.setEventPlace(courseDTO.getEventPlace());

        // Decodificar la imagen de Base64 si está presente
        if (courseDTO.getImage() != null && !courseDTO.getImage().isEmpty()) {
            try {
                // Remover el prefijo de datos si está presente (ejemplo: data:image/jpeg;base64,)
                String base64Image = courseDTO.getImage();
                if (base64Image.startsWith("data:image")) {
                    int commaIndex = base64Image.indexOf(",") + 1;
                    base64Image = base64Image.substring(commaIndex); // Remover el prefijo
                }
                byte[] decodedImage = Base64.getDecoder().decode(base64Image);
                course.setImage(decodedImage);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error al decodificar la imagen: " + e.getMessage());
            }
        } else {
            course.setImage(null);
        }

        course.setStartDate(courseDTO.getStartDate());
        course.setEndDate(courseDTO.getEndDate());
        course.setCreatedAt(courseDTO.getCreatedAt());
        course.setUpdatedAt(courseDTO.getUpdatedAt());

        // Establecer el instructor del curso
        if (courseDTO.getInstructor() != null) {
            Optional<Instructor> instructorOpt = instructorRepository.findById(courseDTO.getInstructor().getId());
            instructorOpt.ifPresent(course::setInstructor);
        }

        Course savedCourse = courseRepository.save(course);

        // Guardar currículos asociados al curso
        if (courseDTO.getCurriculums() != null) {
            List<Curriculum> curriculums = courseDTO.getCurriculums().stream()
                    .map(curriculumDTO -> {
                        Curriculum curriculum = new Curriculum();
                        curriculum.setCourse(savedCourse);
                        curriculum.setSectionName(curriculumDTO.getSectionName());
                        curriculum.setLessonName(curriculumDTO.getLessonName());
                        curriculum.setContent(curriculumDTO.getContent());
                        curriculum.setLessonOrder(curriculumDTO.getOrder());
                        curriculum.setDuration(curriculumDTO.getDuration());
                        curriculum.setType(curriculumDTO.getType());
                        curriculum.setResourceLink(curriculumDTO.getResourceLink());
                        curriculum.setIsMandatory(curriculumDTO.getIsMandatory());
                        return curriculum;
                    }).collect(Collectors.toList());
            curriculumRepository.saveAll(curriculums);
        }

        return convertToDto(savedCourse, savedCourse.getInstructor(), curriculumRepository.findByCourseId(savedCourse.getId()));
    }

    
    @Transactional
    public Optional<CourseDTO> updateCourse(Long id, CourseDTO courseDTO) {
        return courseRepository.findById(id).map(course -> {
            // Actualizar datos del curso
            course.setName(courseDTO.getName());
            course.setDescription(courseDTO.getDescription());
            course.setCategory(courseDTO.getCategory());
            course.setLessonsCount(courseDTO.getLessonsCount());
            course.setStudentsCount(courseDTO.getStudentsCount());
            course.setPrice(courseDTO.getPrice());
            course.setDuration(courseDTO.getDuration());
            course.setLevel(courseDTO.getLevel());
            course.setEventPlace(courseDTO.getEventPlace());

            // Decodificar la imagen de Base64 si está presente
            if (courseDTO.getImage() != null && !courseDTO.getImage().isEmpty()) {
                try {
                    // Remover el prefijo de datos (ejemplo: data:image/jpeg;base64,)
                    String base64Image = courseDTO.getImage();
                    if (base64Image.startsWith("data:image")) {
                        int commaIndex = base64Image.indexOf(",") + 1;
                        base64Image = base64Image.substring(commaIndex);
                    }
                    byte[] decodedImage = Base64.getDecoder().decode(base64Image);
                    course.setImage(decodedImage);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Error al decodificar la imagen: " + e.getMessage());
                }
            } else {
                course.setImage(null); // Si no hay imagen, asignar null
            }

            course.setStartDate(courseDTO.getStartDate());
            course.setEndDate(courseDTO.getEndDate());
            course.setUpdatedAt(courseDTO.getUpdatedAt());

            // Actualizar el instructor del curso
            if (courseDTO.getInstructor() != null) {
                Optional<Instructor> instructorOpt = instructorRepository.findById(courseDTO.getInstructor().getId());
                instructorOpt.ifPresent(course::setInstructor);
            }

            // Guardar el curso actualizado
            Course updatedCourse = courseRepository.save(course);

            // Eliminar currículos anteriores asociados al curso
            curriculumRepository.deleteByCourseId(updatedCourse.getId());

            // Guardar los nuevos currículos
            if (courseDTO.getCurriculums() != null) {
                List<Curriculum> curriculums = courseDTO.getCurriculums().stream()
                        .map(curriculumDTO -> {
                            Curriculum curriculum = new Curriculum();
                            curriculum.setCourse(updatedCourse);
                            curriculum.setSectionName(curriculumDTO.getSectionName());
                            curriculum.setLessonName(curriculumDTO.getLessonName());
                            curriculum.setContent(curriculumDTO.getContent());
                            curriculum.setLessonOrder(curriculumDTO.getOrder());
                            curriculum.setDuration(curriculumDTO.getDuration());
                            curriculum.setType(curriculumDTO.getType());
                            curriculum.setResourceLink(curriculumDTO.getResourceLink());
                            curriculum.setIsMandatory(curriculumDTO.getIsMandatory());
                            return curriculum;
                        }).collect(Collectors.toList());
                curriculumRepository.saveAll(curriculums);
            }

            // Retornar el DTO actualizado del curso
            return convertToDto(updatedCourse, updatedCourse.getInstructor(), curriculumRepository.findByCourseId(updatedCourse.getId()));
        });
    }
    
    // Eliminación lógica de un curso por ID
    public boolean deleteCourse(Long id) {
        return courseRepository.findById(id).map(course -> {
            course.setIsVisible(false);
            courseRepository.save(course);
            return true;
        }).orElse(false);
    }

    // Método para obtener cursos paginados y filtrados por visibilidad
    public Page<CourseDTO> getAllVisibleCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> courses = courseRepository.findByIsVisibleTrueOrderByStartDateAsc(pageable);
        return courses.map(this::convertToBasicDto);
    }

    public CourseDTO getCourseDetailsById(Long id) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();

            // Obtener instructor del curso usando el repositorio
            Instructor instructor = instructorRepository.findById(course.getInstructor().getId()).orElse(null);

            // Obtener currículos asociados al curso
            List<Curriculum> curriculums = curriculumRepository.findByCourseId(course.getId());

            // Convertir entidades a DTO
            return convertToDto(course, instructor, curriculums);
        }
        return null;
    }

    // Convertir entidad básica a DTO
    private CourseDTO convertToBasicDto(Course course) {
        String imageBase64 = course.getImage() != null ? Base64.getEncoder().encodeToString(course.getImage()) : null;
        InstructorDTO instructorDTO = course.getInstructor() != null ? new InstructorDTO(
                course.getInstructor().getId(),
                course.getInstructor().getName(),
                course.getInstructor().getBio(),
                course.getInstructor().getProfileImage() != null ? Base64.getEncoder().encodeToString(course.getInstructor().getProfileImage()) : null
        ) : null;

        return new CourseDTO(
            course.getId(),
            course.getName(),
            course.getDescription(),
            course.getCategory(),
            course.getLessonsCount(),
            course.getStudentsCount(),
            course.getPrice(),
            course.getDuration(),
            course.getLevel(),
            course.getEventPlace(),
            imageBase64,
            instructorDTO,
            course.getStartDate(),
            course.getEndDate(),
            course.getCreatedAt(),
            course.getUpdatedAt(),
            null
        );
    }

    // Convertir entidad completa a DTO
    private CourseDTO convertToDto(Course course, Instructor instructor, List<Curriculum> curriculums) {
        String imageBase64 = null;
        try {
            if (course.getImage() != null) {
                imageBase64 = Base64.getEncoder().encodeToString(course.getImage());
            }
        } catch (Exception e) {
            // Manejo de error de codificación de imagen
            System.err.println("Error al codificar la imagen del curso: " + e.getMessage());
        }

        String instructorImageBase64 = null;
        try {
            if (instructor != null && instructor.getProfileImage() != null) {
                instructorImageBase64 = Base64.getEncoder().encodeToString(instructor.getProfileImage());
            }
        } catch (Exception e) {
            // Manejo de error de codificación de imagen del instructor
            System.err.println("Error al codificar la imagen del instructor: " + e.getMessage());
        }

        InstructorDTO instructorDTO = null;
        if (instructor != null) {
            instructorDTO = new InstructorDTO(
                    instructor.getId(),
                    instructor.getName(),
                    instructor.getBio(),
                    instructorImageBase64
            );
        }

        // Manejo seguro de curriculums
        List<CurriculumDTO> curriculumDTOs = curriculums != null ? curriculums.stream()
                .map(curriculum -> new CurriculumDTO(
                        curriculum.getId(),
                        curriculum.getSectionName(),
                        curriculum.getLessonName(),
                        curriculum.getContent(),
                        curriculum.getLessonOrder(),
                        curriculum.getDuration(),
                        curriculum.getType(),
                        curriculum.getResourceLink(),
                        curriculum.getIsMandatory()
                ))
                .collect(Collectors.toList()) : new ArrayList<>();

        // Devolver el DTO del curso
        return new CourseDTO(
            course.getId(),
            course.getName(),
            course.getDescription(),
            course.getCategory(),
            course.getLessonsCount(),
            course.getStudentsCount(),
            course.getPrice(),
            course.getDuration(),
            course.getLevel(),
            course.getEventPlace(),
            imageBase64,
            instructorDTO,
            course.getStartDate(),
            course.getEndDate(),
            course.getCreatedAt(),
            course.getUpdatedAt(),
            curriculumDTOs
        );
    }
}
