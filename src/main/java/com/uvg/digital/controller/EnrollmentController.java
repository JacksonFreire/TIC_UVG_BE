package com.uvg.digital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.uvg.digital.model.EnrollmentRequest;
import com.uvg.digital.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

	@Autowired
	private EnrollmentService enrollmentService;

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/course/{courseId}")
	public ResponseEntity<String> enrollUserToCourse(@RequestBody EnrollmentRequest enrollmentRequest,
			@PathVariable Long courseId) {
		try {
			Long userId = enrollmentRequest.getUserId();
			enrollmentService.enrollUserToCourse(userId, courseId);
			return ResponseEntity.ok("Usuario inscrito exitosamente en el curso");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PreAuthorize("hasRole('USER')")
    @PostMapping("/event/{eventId}")
    public ResponseEntity<String> enrollUserToEvent(
            @PathVariable Long eventId,
            @RequestBody EnrollmentRequest request) {
        enrollmentService.enrollUserToEvent(request.getUserId(),eventId);
        return ResponseEntity.ok("Inscripción exitosa al evento.");
    }
    
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/isEnrolled")
	public ResponseEntity<Boolean> isUserEnrolledInCouruse(@RequestParam Long courseId, @RequestParam Long userId) {
		boolean isEnrolled = enrollmentService.isUserEnrolledInCourse(courseId, userId);
		return ResponseEntity.ok(isEnrolled);
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/isEnrolledEvent")
	public ResponseEntity<Boolean> isUserEnrolledInEvent(@RequestParam Long eventId, @RequestParam Long userId) {
		boolean isEnrolled = enrollmentService.isUserEnrolledInEvent(eventId, userId);
		return ResponseEntity.ok(isEnrolled);
	}
}