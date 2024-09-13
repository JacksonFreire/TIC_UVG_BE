package com.uvg.digital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Types;
import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(length = 100)
    private String category;

    @Column(name = "lessons_count", nullable = false)
    private int lessonsCount;

    @Column(name = "students_count")
    private Integer studentsCount;

    @Column(nullable = false)
    private double price;

    @Column(length = 50)
    private String duration;

    @Column(length = 50)
    private String level;

    @Column(name = "event_place", length = 255)
    private String eventPlace;

    @Lob
    @JdbcTypeCode(Types.BINARY)  // Usar JdbcTypeCode para especificar que es un tipo binario
    @Column(name = "image", columnDefinition = "BYTEA")
    private byte[] image;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @ManyToOne
    @JoinColumn(name = "instructor_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "courses_instructor_id_fkey"))
    private Instructor instructor;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}