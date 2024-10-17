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

    // Crear un nuevo curso
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
        course.setImage(courseDTO.getImage() != null ? Base64.getDecoder().decode(courseDTO.getImage()) : null);
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

    // Editar un curso existente
    public Optional<CourseDTO> updateCourse(Long id, CourseDTO courseDTO) {
        return courseRepository.findById(id).map(course -> {
            course.setName(courseDTO.getName());
            course.setDescription(courseDTO.getDescription());
            course.setCategory(courseDTO.getCategory());
            course.setLessonsCount(courseDTO.getLessonsCount());
            course.setStudentsCount(courseDTO.getStudentsCount());
            course.setPrice(courseDTO.getPrice());
            course.setDuration(courseDTO.getDuration());
            course.setLevel(courseDTO.getLevel());
            course.setEventPlace(courseDTO.getEventPlace());
            course.setImage(courseDTO.getImage() != null ? Base64.getDecoder().decode(courseDTO.getImage()) : null);
            course.setStartDate(courseDTO.getStartDate());
            course.setEndDate(courseDTO.getEndDate());
            course.setUpdatedAt(courseDTO.getUpdatedAt());
            
            // Actualizar el instructor del curso
            if (courseDTO.getInstructor() != null) {
                Optional<Instructor> instructorOpt = instructorRepository.findById(courseDTO.getInstructor().getId());
                instructorOpt.ifPresent(course::setInstructor);
            }
            
            Course updatedCourse = courseRepository.save(course);

            // Actualizar currículos asociados al curso
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
                        curriculum.getLessonOrder(),
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
