package com.uvg.digital.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uvg.digital.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@GetMapping("/participants/course")
	public ResponseEntity<byte[]> getParticipantCourseReport(@RequestParam Long courseId) {
		try {
			return reportService.generateParticipantReport(courseId);
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body(null);
		}
	}

}
