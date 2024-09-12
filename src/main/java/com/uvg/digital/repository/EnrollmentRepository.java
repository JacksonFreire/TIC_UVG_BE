package com.uvg.digital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.uvg.digital.entity.Enrollment;
import com.uvg.digital.entity.User;
import com.uvg.digital.entity.Course;
import com.uvg.digital.entity.Event;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	
	boolean existsByUserAndCourse(User user, Course course);

	boolean existsByUserAndEvent(User user, Event event);
}
