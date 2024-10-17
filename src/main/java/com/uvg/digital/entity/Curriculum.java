package com.uvg.digital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "curriculums")
public class Curriculum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "curriculums_course_id_fkey"))
    private Course course;

    @Column(name = "section_name", nullable = false, length = 100)
    private String sectionName;

    @Column(name = "lesson_name", nullable = false, length = 100)
    private String lessonName;

    @Column(length = 2000)
    private String content;

    @Column(name = "lesson_order")
    private Integer lessonOrder;

    @Column(length = 50)
    private String duration;

    @Column(length = 50)
    private String type;

    @Column(name = "resource_link", length = 500)
    private String resourceLink;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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