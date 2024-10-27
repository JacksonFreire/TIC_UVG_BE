package com.uvg.digital.model;

import java.time.LocalDateTime;

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
    private String imageUrl; 
    private String instructorName;
    
    public CourseListDTO(Long id, String name, LocalDateTime startDate, LocalDateTime endDate, Double price, String imageUrl, String instructorName) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.imageUrl = imageUrl;
        this.instructorName = instructorName;
    }

}
