package com.uvg.digital.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uvg.digital.entity.Course;
import com.uvg.digital.entity.Enrollment;
import com.uvg.digital.entity.Event;
import com.uvg.digital.entity.User;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

	boolean existsByUserAndCourse(User user, Course course);

	boolean existsByUserAndEvent(User user, Event event);

	boolean existsByCourseIdAndUserId(Long courseId, Long userId);

	boolean existsByEventIdAndUserId(Long eventId, Long userId);

	List<Enrollment> findByCourseId(Long courseId);

	List<Enrollment> findByEventId(Long eventId);

	List<Enrollment> findByCourseIdAndStatus(Long courseId, String status);

	List<Enrollment> findByEventIdAndStatus(Long eventId, String status);

	Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

	Optional<Enrollment> findByUserIdAndEventId(Long userId, Long eventId);

	@Query("SELECT e FROM Enrollment e " +
		       "LEFT JOIN e.course c " +
		       "LEFT JOIN e.event ev " +
		       "WHERE e.status = 'confirmed' AND " +
		       "((c.startDate IS NOT NULL AND c.startDate <= :reminderTime AND c.startDate > CURRENT_TIMESTAMP AND c.isVisible = true) " +
		       "OR (ev.startDate IS NOT NULL AND ev.startDate <= :reminderTime AND ev.startDate > CURRENT_TIMESTAMP AND ev.isVisible = true))")
	List<Enrollment> findConfirmedEnrollmentsWithUpcomingEvents(@Param("reminderTime") LocalDateTime reminderTime);

}
