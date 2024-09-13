package com.uvg.digital.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.uvg.digital.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	
	// Método para obtener cursos visibles, paginados y ordenados por fecha de inicio ascendente
    Page<Course> findByIsVisibleTrueOrderByStartDateAsc(Pageable pageable);
           
}