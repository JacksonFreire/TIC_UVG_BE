package com.uvg.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
