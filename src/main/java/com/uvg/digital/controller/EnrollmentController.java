package com.uvg.digital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uvg.digital.model.EnrollmentRequest;
import com.uvg.digital.service.EnrollmentService;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping("/course/{courseId}")
    public ResponseEntity<String> enrollUserToCourse(@RequestBody EnrollmentRequest enrollmentRequest, @PathVariable Long courseId) {
        try {
            Long userId = enrollmentRequest.getUserId();
            enrollmentService.enrollUserToCourse(userId, courseId);
            return ResponseEntity.ok("Usuario inscrito exitosamente en el curso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/event/{eventId}")
    public ResponseEntity<String> enrollUserToEvent(@RequestParam Long userId, @PathVariable Long eventId) {
        try {
            enrollmentService.enrollUserToEvent(userId, eventId);
            return ResponseEntity.ok("Usuario inscrito exitosamente en el evento");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}