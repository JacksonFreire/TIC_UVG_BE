package com.uvg.digital.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.uvg.digital.entity.Enrollment;
import com.uvg.digital.repository.EnrollmentRepository;

@Service
public class ReminderScheduler {

	@Autowired
	private EnrollmentRepository enrollmentRepository;

	@Autowired
	private NotificationService notificationService;

	@Scheduled(cron = "0 0 9 * * *") // Ejecuta la tarea todos los días a las 9 AM
	public void sendEventAndCourseReminders() {
		LocalDateTime reminderTime = LocalDateTime.now().plusDays(1);

		// Buscar inscripciones confirmadas para eventos próximos
		List<Enrollment> upcomingEnrollments = enrollmentRepository
				.findConfirmedEnrollmentsWithUpcomingEvents(reminderTime);

		for (Enrollment enrollment : upcomingEnrollments) {
			
			System.out.println("Enviando recordatorio para el usuario: " + enrollment.getUser().getEmail() +
	                   " - Curso/Event: " + (enrollment.getCourse() != null ? enrollment.getCourse().getName() : enrollment.getEvent().getName()));
			
			if (enrollment.getCourse() != null) {
				notificationService.sendEventOrCourseReminder(enrollment.getUser(), enrollment.getCourse(), null);
			} else if (enrollment.getEvent() != null) {
				notificationService.sendEventOrCourseReminder(enrollment.getUser(), null, enrollment.getEvent());
			}
		}
	}

}
