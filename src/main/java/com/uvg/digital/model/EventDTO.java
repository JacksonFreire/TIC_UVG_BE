package com.uvg.digital.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Double price;
    private String imageUrl; // Imagen en base64
    private String category;
    private String additionalDetails;
    private InstructorDTO instructor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
