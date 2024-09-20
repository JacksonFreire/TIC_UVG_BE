package com.uvg.digital.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.digital.entity.Event;
import com.uvg.digital.model.CourseDTO;
import com.uvg.digital.service.CourseService;
import com.uvg.digital.service.EventService;

@RestController
@RequestMapping("/api/available")
public class AvailabilityController {

	@Autowired
	private CourseService courseService;

	@Autowired
	private EventService eventService;

	@GetMapping("/courses")
	public ResponseEntity<Page<CourseDTO>> getAllVisibleCourses(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "6") int size) {
		Page<CourseDTO> courses = courseService.getAllVisibleCourses(page, size);
		return ResponseEntity.ok(courses);
	}

	@GetMapping("/courses/{id}")
	public ResponseEntity<CourseDTO> getCourseDetailsById(@PathVariable Long id) {
		CourseDTO courseDTO = courseService.getCourseDetailsById(id);
		if (courseDTO != null) {
			return ResponseEntity.ok(courseDTO);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/events")
	public ResponseEntity<Page<Event>> getAllVisibleEvents(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size) {
		Page<Event> events = eventService.getAllVisibleEvents(page, size);
		return ResponseEntity.ok(events);
	}

	@GetMapping("/events/{id}")
	public ResponseEntity<Event> getEventById(@PathVariable Long id) {
		Optional<Event> event = eventService.getEventById(id);
		return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}
}
