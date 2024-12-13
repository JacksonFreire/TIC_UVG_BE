package com.uvg.digital.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
	 private Long id;
	    private String name;
	    private String description;
	    private String category;
	    private int lessonsCount;
	    private Integer studentsCount;
	    private double price;
	    private String duration;
	    private String level;
	    private String eventPlace;
	    private String imageUrl; 
	    private InstructorDTO instructor; // Detalles del instructor
	    private LocalDateTime startDate;
	    private LocalDateTime endDate;
	    private LocalDateTime createdAt;
	    private LocalDateTime updatedAt;
	    private List<CurriculumDTO> curriculums; // Lista de detalles del currículo
}