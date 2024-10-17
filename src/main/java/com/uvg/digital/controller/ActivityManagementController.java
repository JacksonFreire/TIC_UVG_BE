package com.uvg.digital.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.digital.model.CourseDTO;
import com.uvg.digital.model.EventDTO;
import com.uvg.digital.service.CourseService;
import com.uvg.digital.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityManagementController {

    @Autowired
    private CourseService courseService;
    
    @Autowired
	private EventService eventService;

    // Obtener todos los cursos visibles con paginación
    @GetMapping("/courses/list")
    public ResponseEntity<Page<CourseDTO>> getAllVisibleCourses(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "6") int size) {
        Page<CourseDTO> courses = courseService.getAllVisibleCourses(page, size);
        return ResponseEntity.ok(courses);
    }

    // Obtener un curso por ID
    @GetMapping("/courses/details/{courseId}")
    public ResponseEntity<CourseDTO> getCourseDetailsById(@PathVariable("courseId") Long id) {
        CourseDTO courseDTO = courseService.getCourseDetailsById(id);
        if (courseDTO != null) {
            return ResponseEntity.ok(courseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear un nuevo curso
    @PostMapping("/courses/create")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    // Editar un curso existente
    @PutMapping("/courses/update/{courseId}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable("courseId") Long id, @RequestBody CourseDTO courseDTO) {
        Optional<CourseDTO> updatedCourse = courseService.updateCourse(id, courseDTO);
        return updatedCourse.map(course -> new ResponseEntity<>(course, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Eliminar un curso por ID
    @DeleteMapping("/courses/delete/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("courseId") Long id) {
        boolean isDeleted = courseService.deleteCourse(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Obtener todos los eventos visibles con paginación
    @GetMapping("/events")
    public ResponseEntity<Page<EventDTO>> getAllVisibleEvents(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "5") int size) {
        Page<EventDTO> events = eventService.getAllVisibleEvents(page, size);
        return ResponseEntity.ok(events);
    }

    // Obtener un evento por ID
    @GetMapping("/events/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        Optional<EventDTO> event = eventService.getEventById(id);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo evento
    @PostMapping("/events/create")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        EventDTO createdEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    // Editar un evento existente
    @PutMapping("/events/update/{eventId}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable("eventId") Long id, @RequestBody EventDTO eventDTO) {
        Optional<EventDTO> updatedEvent = eventService.updateEvent(id, eventDTO);
        return updatedEvent.map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Eliminar un evento por ID
    @DeleteMapping("/events/delete/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("eventId") Long id) {
        boolean isDeleted = eventService.deleteEvent(id);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
