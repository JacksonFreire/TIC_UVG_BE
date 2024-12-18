
package com.uvg.digital.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uvg.digital.entity.Course;
import com.uvg.digital.entity.Curriculum;
import com.uvg.digital.entity.Instructor;
import com.uvg.digital.model.CourseDTO;
import com.uvg.digital.model.CourseListDTO;
import com.uvg.digital.model.CurriculumDTO;
import com.uvg.digital.model.InstructorDTO;
import com.uvg.digital.repository.CourseRepository;
import com.uvg.digital.repository.CurriculumRepository;
import com.uvg.digital.repository.InstructorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

	@Autowired
	private final CourseRepository courseRepository;

	@Autowired
	private InstructorRepository instructorRepository;

	@Autowired
	private CurriculumRepository curriculumRepository;

	@Autowired
	private BlobStorageService blobStorageService;

	@Autowired
	private NotificationService notificationService;

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

		// Subir la imagen a Blob Storage y obtener la URL
		if (courseDTO.getImageUrl() != null && !courseDTO.getImageUrl().isEmpty()) {
			try {
				String base64Image = courseDTO.getImageUrl();
				if (base64Image.startsWith("data:image")) {
					int commaIndex = base64Image.indexOf(",") + 1;
					base64Image = base64Image.substring(commaIndex);
				}
				byte[] decodedImage = Base64.getDecoder().decode(base64Image);
				String imageUrl = blobStorageService.uploadImage(decodedImage, "imagenes-cursos",
						"curso-" + courseDTO.getName() + ".jpg");
				course.setImageUrl(imageUrl); // Guardar solo la URL de la imagen
			} catch (Exception e) {
				throw new RuntimeException("Error al cargar la imagen en Blob Storage: " + e.getMessage());
			}
		}

		course.setStartDate(courseDTO.getStartDate());
		course.setEndDate(courseDTO.getEndDate());
		course.setCreatedAt(courseDTO.getCreatedAt());
		course.setUpdatedAt(courseDTO.getUpdatedAt());

		// Establecer el instructor del curso
		if (courseDTO.getInstructor() != null) {
			Optional<Instructor> instructorOpt = instructorRepository.findById(courseDTO.getInstructor().getId());
			instructorOpt.ifPresent(instructor -> {
				course.setInstructor(instructor);

				// Notificar sobre asignación del curso
				notificationService.notifyCourseAssignment(instructor, course);
			});
		}

		Course savedCourse = courseRepository.save(course);

		// Guardar currículos asociados al curso
		if (courseDTO.getCurriculums() != null) {
			List<Curriculum> curriculums = courseDTO.getCurriculums().stream().map(curriculumDTO -> {
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

		return convertToDto(savedCourse, savedCourse.getInstructor(),
				curriculumRepository.findByCourseId(savedCourse.getId()));
	}

	@Transactional
	public Optional<CourseDTO> updateCourse(Long id, CourseDTO courseDTO) {
		return courseRepository.findById(id).map(course -> {

			StringBuilder changes = new StringBuilder(); // Para registrar los cambios importantes

			// Verificar cambios en la fecha de inicio
			if (!courseDTO.getStartDate().equals(course.getStartDate())) {
				changes.append("• Fecha de inicio: ").append(course.getStartDate()).append(" → ")
						.append(courseDTO.getStartDate()).append("\n");
				course.setStartDate(courseDTO.getStartDate());
			}

			// Verificar cambios en la fecha de finalización
			if (!courseDTO.getEndDate().equals(course.getEndDate())) {
				changes.append("• Fecha de finalización: ").append(course.getEndDate()).append(" → ")
						.append(courseDTO.getEndDate()).append("\n");
				course.setEndDate(courseDTO.getEndDate());
			}

			// Verificar cambios en el lugar del evento
			if (!courseDTO.getEventPlace().equals(course.getEventPlace())) {
				changes.append("• Lugar del evento: ").append(course.getEventPlace()).append(" → ")
						.append(courseDTO.getEventPlace()).append("\n");
				course.setEventPlace(courseDTO.getEventPlace());
			}

			course.setName(courseDTO.getName());
			course.setDescription(courseDTO.getDescription());
			course.setCategory(courseDTO.getCategory());
			course.setLessonsCount(courseDTO.getLessonsCount());
			course.setStudentsCount(courseDTO.getStudentsCount());
			course.setPrice(courseDTO.getPrice());
			course.setDuration(courseDTO.getDuration());
			course.setLevel(courseDTO.getLevel());
			course.setEventPlace(courseDTO.getEventPlace());

			// Actualizar imagen en Blob Storage si es necesario
			if (courseDTO.getImageUrl() != null && !courseDTO.getImageUrl().isEmpty()) {
				String imageUrl = courseDTO.getImageUrl().trim();

				// Verificar si la cadena parece ser una URL o una base64
				if (imageUrl.startsWith("http") || imageUrl.startsWith("https")) {
					// Es una URL, simplemente guardarla sin intentar decodificar
					course.setImageUrl(imageUrl);
				} else {
					// Si no es una URL, asumimos que es base64 y eliminamos el prefijo si existe
					try {
						if (imageUrl.startsWith("data:image")) {
							int commaIndex = imageUrl.indexOf(",") + 1;
							imageUrl = imageUrl.substring(commaIndex);
						}

						byte[] decodedImage = Base64.getDecoder().decode(imageUrl);
						// Subir la imagen decodificada al Blob Storage y obtener la URL
						String uploadedImageUrl = blobStorageService.uploadImage(decodedImage, "imagenes-cursos",
								"curso-" + courseDTO.getName() + ".jpg");
						course.setImageUrl(uploadedImageUrl);

					} catch (IllegalArgumentException e) {
						throw new RuntimeException("Error al decodificar la imagen: la cadena base64 no es válida.", e);
					} catch (Exception e) {
						throw new RuntimeException("Error al cargar la imagen en Blob Storage: " + e.getMessage(), e);
					}
				}
			}

			course.setStartDate(courseDTO.getStartDate());
			course.setEndDate(courseDTO.getEndDate());
			course.setUpdatedAt(courseDTO.getUpdatedAt());

			// Actualizar el instructor del curso
			if (courseDTO.getInstructor() != null) {
				Optional<Instructor> instructorOpt = instructorRepository.findById(courseDTO.getInstructor().getId());
				instructorOpt.ifPresent(course::setInstructor);
			}

			Course updatedCourse = courseRepository.save(course);

			// Eliminar currículos anteriores asociados al curso
			curriculumRepository.deleteByCourseId(updatedCourse.getId());

			// Guardar los nuevos currículos
			if (courseDTO.getCurriculums() != null) {
				List<Curriculum> curriculums = courseDTO.getCurriculums().stream().map(curriculumDTO -> {
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

			// Notificar al instructor si hubo cambios importantes
			if (changes.length() > 0 && updatedCourse.getInstructor() != null) {
				notificationService.sendCourseUpdateNotification(updatedCourse.getInstructor().getUser(), updatedCourse,
						changes.toString());
			}

			return convertToDto(updatedCourse, updatedCourse.getInstructor(),
					curriculumRepository.findByCourseId(updatedCourse.getId()));
		});
	}

	public boolean deleteCourse(Long id) {
		return courseRepository.findById(id).map(course -> {
			course.setIsVisible(false);
			courseRepository.save(course);

			// Notificar al instructor sobre la eliminación
			if (course.getInstructor() != null) {
				notificationService.notifyCourseDeletion(course);
			}

			return true;
		}).orElse(false);
	}

	@Cacheable("courses")
	public Page<CourseListDTO> getAllVisibleCourses(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").ascending());
		return courseRepository.findAllVisibleCourses(pageable);
	}

	public CourseDTO getCourseDetailsById(Long id) {
		Optional<Course> courseOpt = courseRepository.findById(id);
		if (courseOpt.isPresent()) {
			Course course = courseOpt.get();
			Instructor instructor = instructorRepository.findById(course.getInstructor().getId()).orElse(null);
			List<Curriculum> curriculums = curriculumRepository.findByCourseId(course.getId());
			return convertToDto(course, instructor, curriculums);
		}
		return null;
	}

	// Obtener cursos de un instructor por ID
	public List<CourseListDTO> getCoursesByInstructorId(Long idInstructor) {
		// Verificar que el instructor exista antes de buscar los cursos
		instructorRepository.findById(idInstructor)
				.orElseThrow(() -> new RuntimeException("Instructor no encontrado con el ID: " + idInstructor));

		// Usar el método del repositorio para obtener los cursos
		return courseRepository.findVisibleCoursesByInstructorId(idInstructor);
	}

	private CourseDTO convertToDto(Course course, Instructor instructor, List<Curriculum> curriculums) {
		InstructorDTO instructorDTO = instructor != null ? new InstructorDTO(instructor.getId(),
				instructor.getUser().getFirstName() + " " + instructor.getUser().getLastName(), instructor.getBio(),
				instructor.getProfileImage() != null ? Base64.getEncoder().encodeToString(instructor.getProfileImage())
						: null)
				: null;

		List<CurriculumDTO> curriculumDTOs = curriculums != null ? curriculums.stream()
				.map(curriculum -> new CurriculumDTO(curriculum.getId(), curriculum.getSectionName(),
						curriculum.getLessonName(), curriculum.getContent(), curriculum.getLessonOrder(),
						curriculum.getDuration(), curriculum.getType(), curriculum.getResourceLink(),
						curriculum.getIsMandatory()))
				.collect(Collectors.toList()) : new ArrayList<>();

		return new CourseDTO(course.getId(), course.getName(), course.getDescription(), course.getCategory(),
				course.getLessonsCount(), course.getStudentsCount(), course.getPrice(), course.getDuration(),
				course.getLevel(), course.getEventPlace(), course.getImageUrl(), // Utilizar la URL de la imagen
				instructorDTO, course.getStartDate(), course.getEndDate(), course.getCreatedAt(), course.getUpdatedAt(),
				curriculumDTOs);
	}
}
