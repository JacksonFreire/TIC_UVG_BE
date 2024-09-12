package com.uvg.digital.service;

import com.uvg.digital.entity.Course;
import com.uvg.digital.entity.Instructor;
import com.uvg.digital.entity.Curriculum;
import com.uvg.digital.model.CourseDTO;
import com.uvg.digital.model.CurriculumDTO;
import com.uvg.digital.model.InstructorDTO;
import com.uvg.digital.repository.CourseRepository;
import com.uvg.digital.repository.InstructorRepository;
import com.uvg.digital.repository.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CurriculumRepository curriculumRepository;

    /**
     * Obtener lista de todos los cursos disponibles.
     *
     * @return Lista de CourseDTO
     */
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener detalles completos de un curso por su ID.
     *
     * @param id ID del curso
     * @return CourseDTO con toda la información del curso, instructor y currículum
     */
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

    /**
     * Convertir entidad Course a CourseDTO para listas básicas (sin detalles completos).
     *
     * @param course Entidad del curso
     * @return CourseDTO básico
     */
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
            null // Lista de currículos nula para el listado básico
        );
    }

    /**
     * Convertir entidad Course, Instructor y lista de Curriculum a CourseDTO con detalles completos.
     *
     * @param course       Entidad del curso
     * @param instructor   Entidad del instructor
     * @param curriculums  Lista de entidades de currículum
     * @return CourseDTO con detalles completos
     */
    private CourseDTO convertToDto(Course course, Instructor instructor, List<Curriculum> curriculums) {
        String imageBase64 = course.getImage() != null ? Base64.getEncoder().encodeToString(course.getImage()) : null;
        String instructorImageBase64 = instructor != null && instructor.getProfileImage() != null ? 
                                        Base64.getEncoder().encodeToString(instructor.getProfileImage()) : null;

        InstructorDTO instructorDTO = instructor != null ? new InstructorDTO(
                instructor.getId(),
                instructor.getName(),
                instructor.getBio(),
                instructorImageBase64
        ) : null;

        List<CurriculumDTO> curriculumDTOs = curriculums.stream()
                .map(curriculum -> new CurriculumDTO(
                        curriculum.getId(),
                        curriculum.getSectionName(),
                        curriculum.getLessonName(),
                        curriculum.getContent(),
                        curriculum.getOrder(),
                        curriculum.getDuration(),
                        curriculum.getType(),
                        curriculum.getResourceLink(),
                        curriculum.getIsMandatory()
                ))
                .collect(Collectors.toList());

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
