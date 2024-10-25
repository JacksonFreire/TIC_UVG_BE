package com.uvg.digital.model;

import java.time.LocalDateTime;
import java.util.Base64;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseListDTO {
    private Long id;
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double price;
    private String image;  // Base64 string
    private String instructorName;

    // Constructor adicional que convierte byte[] a base64
    public CourseListDTO(Long id, String name, LocalDateTime startDate, LocalDateTime endDate, double price, byte[] imageBytes, String instructorName) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.image = imageBytes != null ? Base64.getEncoder().encodeToString(imageBytes) : null;
        this.instructorName = instructorName;
    }
}
