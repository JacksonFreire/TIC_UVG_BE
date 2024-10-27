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
import com.uvg.digital.model.EventDTO;
import com.uvg.digital.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BlobStorageService blobStorageService;

    // Obtener todos los eventos visibles con paginación
    public Page<EventDTO> getAllVisibleEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = eventRepository.findByIsVisibleTrueOrderByStartDateAsc(pageable);
        List<EventDTO> eventDTOs = eventsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new PageImpl<>(eventDTOs, pageable, eventsPage.getTotalElements());
    }

    // Obtener un evento por ID
    public Optional<EventDTO> getEventById(Long id) {
        return eventRepository.findById(id).map(this::convertToDto);
    }

    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setName(eventDTO.getName());
        event.setDescription(eventDTO.getDescription());
        event.setStartDate(eventDTO.getStartDate());
        event.setEndDate(eventDTO.getEndDate());
        event.setLocation(eventDTO.getLocation());
        event.setPrice(eventDTO.getPrice());

        // Subir la imagen a Blob Storage y obtener la URL
        if (eventDTO.getImageUrl() != null && !eventDTO.getImageUrl().isEmpty()) {
            try {
                String base64Image = eventDTO.getImageUrl();
                if (base64Image.startsWith("data:image")) {
                    int commaIndex = base64Image.indexOf(",") + 1;
                    base64Image = base64Image.substring(commaIndex); // Remover el prefijo
                }
                byte[] decodedImage = Base64.getDecoder().decode(base64Image);
                String imageUrl = blobStorageService.uploadImage(decodedImage, "imagenes-eventos", "evento-" + eventDTO.getName() + ".jpg");
                event.setImageUrl(imageUrl); // Guardar solo la URL de la imagen
            } catch (Exception e) {
                throw new RuntimeException("Error al cargar la imagen en Blob Storage: " + e.getMessage());
            }
        }

        event.setCategory(eventDTO.getCategory());
        event.setAdditionalDetails(eventDTO.getAdditionalDetails());
        event.setIsVisible(true);

        // Guardar el evento en la base de datos
        Event savedEvent = eventRepository.save(event);

        return convertToDto(savedEvent);
    }

    @Transactional
    public Optional<EventDTO> updateEvent(Long id, EventDTO eventDTO) {
        return eventRepository.findById(id).map(event -> {
            event.setName(eventDTO.getName());
            event.setDescription(eventDTO.getDescription());
            event.setStartDate(eventDTO.getStartDate());
            event.setEndDate(eventDTO.getEndDate());
            event.setLocation(eventDTO.getLocation());
            event.setPrice(eventDTO.getPrice());

            // Actualizar imagen en Blob Storage si es necesario
            if (eventDTO.getImageUrl() != null && !eventDTO.getImageUrl().isEmpty()) {
                try {
                    String base64Image = eventDTO.getImageUrl();
                    if (base64Image.startsWith("data:image")) {
                        int commaIndex = base64Image.indexOf(",") + 1;
                        base64Image = base64Image.substring(commaIndex);
                    }
                    byte[] decodedImage = Base64.getDecoder().decode(base64Image);
                    String imageUrl = blobStorageService.uploadImage(decodedImage, "imagenes-eventos", "evento-" + eventDTO.getName() + ".jpg");
                    event.setImageUrl(imageUrl); // Actualizar la URL de la imagen
                } catch (Exception e) {
                    throw new RuntimeException("Error al cargar la imagen en Blob Storage: " + e.getMessage());
                }
            }

            event.setCategory(eventDTO.getCategory());
            event.setAdditionalDetails(eventDTO.getAdditionalDetails());

            // Guardar el evento actualizado en la base de datos
            Event updatedEvent = eventRepository.save(event);

            return convertToDto(updatedEvent);
        });
    }

    // Eliminación lógica de un evento por ID
    public boolean deleteEvent(Long id) {
        return eventRepository.findById(id).map(event -> {
            event.setIsVisible(false);
            eventRepository.save(event);
            return true;
        }).orElse(false);
    }

    // Convertir entidad a DTO
    private EventDTO convertToDto(Event event) {
        return new EventDTO(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getLocation(),
                event.getPrice(),
                event.getImageUrl(), // Utilizar la URL de la imagen
                event.getCategory(),
                event.getAdditionalDetails(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }
}
