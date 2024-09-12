package com.uvg.digital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.uvg.digital.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
           
}