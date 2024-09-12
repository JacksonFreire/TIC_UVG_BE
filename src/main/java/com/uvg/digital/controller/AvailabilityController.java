package com.uvg.digital.controller;

import com.uvg.digital.model.CourseDTO;
import com.uvg.digital.entity.Event;
import com.uvg.digital.service.CourseService;
import com.uvg.digital.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/available")
public class AvailabilityController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EventService eventService;

    /**
     * Endpoint para obtener la lista de todos los cursos disponibles.
     *
     * @return Lista de CourseDTO
     */
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> getAllAvailableCourses() {
        List<CourseDTO> courseDTOs = courseService.getAllCourses();
        return ResponseEntity.ok(courseDTOs);
    }

    /**
     * Endpoint para obtener los detalles completos de un curso por su ID.
     *
     * @param id ID del curso
     * @return CourseDTO con detalles completos del curso
     */
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> getCourseDetailsById(@PathVariable Long id) {
        CourseDTO courseDTO = courseService.getCourseDetailsById(id);
        if (courseDTO != null) {
            return ResponseEntity.ok(courseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para obtener la lista de todos los eventos disponibles.
     *
     * @return Lista de eventos
     */
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getAllAvailableEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
}
