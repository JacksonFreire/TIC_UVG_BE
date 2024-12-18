package com.uvg.digital.service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uvg.digital.entity.Event;
import com.uvg.digital.entity.Instructor;
import com.uvg.digital.model.EventDTO;
import com.uvg.digital.model.InstructorDTO;
import com.uvg.digital.repository.EventRepository;
import com.uvg.digital.repository.InstructorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private BlobStorageService blobStorageService;

	@Autowired
	private InstructorRepository instructorRepository;

	@Autowired
	private NotificationService notificationService;

	// Obtener todos los eventos visibles con paginación
	public Page<EventDTO> getAllVisibleEvents(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Event> eventsPage = eventRepository.findByIsVisibleTrueOrderByStartDateAsc(pageable);
		List<EventDTO> eventDTOs = eventsPage.getContent().stream().map(this::convertToDTO) // Usamos convertToDTO
				.collect(Collectors.toList());
		return new PageImpl<>(eventDTOs, pageable, eventsPage.getTotalElements());
	}

	// Obtener un evento por ID
	public Optional<EventDTO> getEventById(Long id) {
		return eventRepository.findById(id).map(this::convertToDTO); // Usamos convertToDTO
	}

	// Crear un nuevo evento
	@Transactional
	public EventDTO createEvent(EventDTO eventDTO) {
		Event event = new Event();
		event.setName(eventDTO.getName());
		event.setDescription(eventDTO.getDescription());
		event.setStartDate(eventDTO.getStartDate());
		event.setEndDate(eventDTO.getEndDate());
		event.setLocation(eventDTO.getLocation());
		event.setPrice(eventDTO.getPrice());

		// Asignar instructor
		Instructor instructor = instructorRepository.findById(eventDTO.getInstructor().getId()).orElseThrow(
				() -> new RuntimeException("Instructor no encontrado con ID: " + eventDTO.getInstructor().getId()));
		event.setInstructor(instructor);

		// Procesar imagen si existe
		if (eventDTO.getImageUrl() != null && !eventDTO.getImageUrl().isEmpty()) {
			try {
				String base64Image = eventDTO.getImageUrl();
				if (base64Image.startsWith("data:image")) {
					int commaIndex = base64Image.indexOf(",") + 1;
					base64Image = base64Image.substring(commaIndex);
				}
				byte[] decodedImage = Base64.getDecoder().decode(base64Image);
				String imageUrl = blobStorageService.uploadImage(decodedImage, "imagenes-eventos",
						"evento-" + eventDTO.getName() + ".jpg");
				event.setImageUrl(imageUrl);
			} catch (Exception e) {
				throw new RuntimeException("Error al cargar la imagen en Blob Storage: " + e.getMessage());
			}
		}

		event.setCategory(eventDTO.getCategory());
		event.setAdditionalDetails(eventDTO.getAdditionalDetails());
		event.setIsVisible(true);

		Event savedEvent = eventRepository.save(event);

		// Notificar al instructor sobre el evento creado
		notificationService.sendEventCreatedNotification(instructor.getUser(), savedEvent);

		return convertToDTO(savedEvent);
	}

	// Actualizar un evento existente
	@Transactional
	public Optional<EventDTO> updateEvent(Long id, EventDTO eventDTO) {
	    return eventRepository.findById(id).map(event -> {

	        StringBuilder changes = new StringBuilder(); // Para registrar los cambios importantes

	        // Verificar cambios en la fecha de inicio
	        if (!eventDTO.getStartDate().equals(event.getStartDate())) {
	            changes.append("• Fecha de inicio: ").append(event.getStartDate()).append(" → ")
	                    .append(eventDTO.getStartDate()).append("\n");
	            event.setStartDate(eventDTO.getStartDate());
	        }

	        // Verificar cambios en la fecha de finalización
	        if (!eventDTO.getEndDate().equals(event.getEndDate())) {
	            changes.append("• Fecha de finalización: ").append(event.getEndDate()).append(" → ")
	                    .append(eventDTO.getEndDate()).append("\n");
	            event.setEndDate(eventDTO.getEndDate());
	        }

	        // Verificar cambios en el lugar del evento
	        if (!eventDTO.getLocation().equals(event.getLocation())) {
	            changes.append("• Ubicación: ").append(event.getLocation()).append(" → ")
	                    .append(eventDTO.getLocation()).append("\n");
	            event.setLocation(eventDTO.getLocation());
	        }

	        // Actualizar otros campos generales
	        event.setName(eventDTO.getName());
	        event.setDescription(eventDTO.getDescription());
	        event.setCategory(eventDTO.getCategory());
	        event.setPrice(eventDTO.getPrice());
	        event.setAdditionalDetails(eventDTO.getAdditionalDetails());

	        // Actualizar imagen si es necesario
	        if (eventDTO.getImageUrl() != null && !eventDTO.getImageUrl().isEmpty()) {
	            String imageUrl = eventDTO.getImageUrl().trim();
	            try {
	                if (imageUrl.startsWith("http") || imageUrl.startsWith("https")) {
	                    event.setImageUrl(imageUrl);
	                } else {
	                    if (imageUrl.startsWith("data:image")) {
	                        int commaIndex = imageUrl.indexOf(",") + 1;
	                        imageUrl = imageUrl.substring(commaIndex);
	                    }
	                    byte[] decodedImage = Base64.getDecoder().decode(imageUrl);
	                    String uploadedImageUrl = blobStorageService.uploadImage(decodedImage, "imagenes-eventos",
	                            "evento-" + eventDTO.getName() + ".jpg");
	                    event.setImageUrl(uploadedImageUrl);
	                }
	            } catch (IllegalArgumentException e) {
	                throw new RuntimeException("Error al decodificar la imagen: la cadena base64 no es válida.", e);
	            } catch (Exception e) {
	                throw new RuntimeException("Error al cargar la imagen en Blob Storage: " + e.getMessage(), e);
	            }
	        }

	        // Validar y actualizar instructor si es necesario
	        if (eventDTO.getInstructor() != null && eventDTO.getInstructor().getId() != null) {
	            Instructor newInstructor = instructorRepository.findById(eventDTO.getInstructor().getId())
	                    .orElseThrow(() -> new RuntimeException("Instructor no encontrado con ID: " + eventDTO.getInstructor().getId()));
	            if (!event.getInstructor().equals(newInstructor)) {
	                changes.append("• Instructor cambiado.\n");
	                event.setInstructor(newInstructor);
	            }
	        }

	        // Guardar el evento actualizado
	        Event updatedEvent = eventRepository.save(event);

	        // Notificar al instructor si hubo cambios importantes
	        if (changes.length() > 0 && updatedEvent.getInstructor() != null) {
	            notificationService.sendEventUpdateNotification(updatedEvent.getInstructor(), updatedEvent, changes.toString());
	        }

	        return convertToDTO(updatedEvent);
	    });
	}


	// Eliminación lógica de un evento por ID
	public boolean deleteEvent(Long id) {
		return eventRepository.findById(id).map(event -> {
			event.setIsVisible(false);
			eventRepository.save(event);

			// Notificar al instructor sobre eliminación
			notificationService.sendEventDeletedNotification(event.getInstructor().getUser(), event);

			return true;
		}).orElse(false);
	}

	// Obtener eventos asignados a un instructor
	public List<EventDTO> getEventsByInstructorId(Long instructorId) {
		// Verificar si el instructor existe
		instructorRepository.findById(instructorId)
				.orElseThrow(() -> new RuntimeException("Instructor no encontrado con el ID: " + instructorId));

		// Obtener los eventos del repositorio
		List<Event> events = eventRepository.findByInstructorIdAndIsVisibleTrue(instructorId);

		// Convertir a DTOs
		return events.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// Convertir entidad a DTO
	private EventDTO convertToDTO(Event event) {
		// Convertir Instructor a DTO
		InstructorDTO instructorDTO = null;
		if (event.getInstructor() != null) {
			instructorDTO = new InstructorDTO(event.getInstructor().getId(),
					event.getInstructor().getUser().getFirstName() + " "
							+ event.getInstructor().getUser().getLastName(),
					event.getInstructor().getBio(),
					event.getInstructor().getProfileImage() != null
							? Base64.getEncoder().encodeToString(event.getInstructor().getProfileImage()) // Convertir a
																											// Base64
							: null);
		}

		// Construir EventDTO con Instructor
		return new EventDTO(event.getId(), event.getName(), event.getDescription(), event.getStartDate(),
				event.getEndDate(), event.getLocation(), event.getPrice(), event.getImageUrl(), event.getCategory(),
				event.getAdditionalDetails(), instructorDTO, // Incluir el instructor
				event.getCreatedAt(), event.getUpdatedAt());
	}

}
