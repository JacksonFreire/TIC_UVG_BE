package com.uvg.digital.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uvg.digital.entity.Course;
import com.uvg.digital.model.CourseListDTO;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	/*
	List<Course> findAllByImageIsNotNull(); */
    
    	// Método con proyección DTO para obtener cursos visibles
	@Query("SELECT new com.uvg.digital.model.CourseListDTO(c.id, c.name, c.startDate, c.endDate, c.price, c.imageUrl, i.name) " +
		       "FROM Course c JOIN c.instructor i WHERE c.isVisible = true ORDER BY c.startDate ASC")
		Page<CourseListDTO> findAllVisibleCourses(Pageable pageable);

           
}
