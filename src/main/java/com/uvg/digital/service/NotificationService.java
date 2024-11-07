package com.uvg.digital.service;

import org.springframework.stereotype.Service;
import com.uvg.digital.entity.User;
import com.uvg.digital.entity.Course;
import com.uvg.digital.entity.Event;

@Service
public class NotificationService {

	private final AzureEmailService emailService;

	public NotificationService(AzureEmailService emailService) {
		this.emailService = emailService;
	}

	public void sendEnrollmentConfirmation(User user, Course course, Event event) {
		String subject;
		String body;

		if (course != null) {
			subject = "Confirmación de Inscripción al Curso: " + course.getName();
			body = "Estimado " + user.getFirstName() + ",\n\n" + "Te has inscrito exitosamente en el curso: "
					+ course.getName() + ".\n" + "Fecha de inicio: " + course.getStartDate() + "\n"
					+ "Fecha de finalización: " + course.getEndDate() + "\n\n"
					+ "Gracias por ser parte de nuestra comunidad.\n";
		} else if (event != null) {
			subject = "Confirmación de Inscripción al Evento: " + event.getName();
			body = "Estimado " + user.getFirstName() + ",\n\n" + "Te has inscrito exitosamente en el evento: "
					+ event.getName() + ".\n" + "Fecha del evento: " + event.getStartDate() + "\n" + "Lugar: "
					+ event.getLocation() + "\n\n" + "Gracias por ser parte de nuestra comunidad.\n";
		} else {
			subject = "Confirmación de Inscripción";
			body = "Estimado " + user.getFirstName() + ",\n\n" + "Tu inscripción ha sido procesada correctamente.\n\n"
					+ "Gracias por ser parte de nuestra comunidad.\n";
		}

		emailService.sendEmail(user.getEmail(), subject, body);
	}

	public void notifyAdminOfNewEnrollment(Course course, Event event, User user) {
		String adminEmail = "info@univeritasgroup.com";
		String subject;
		String body;

		if (course != null) {
			subject = "Nueva Inscripción al Curso: " + course.getName();
			body = "El usuario " + user.getFirstName() + " " + user.getLastName() + " se ha inscrito en el curso: "
					+ course.getName() + ".\n" + "Correo electrónico del usuario: " + user.getEmail() + "\n";
		} else if (event != null) {
			subject = "Nueva Inscripción al Evento: " + event.getName();
			body = "El usuario " + user.getFirstName() + " " + user.getLastName() + " se ha inscrito en el evento: "
					+ event.getName() + ".\n" + "Correo electrónico del usuario: " + user.getEmail() + "\n";
		} else {
			subject = "Nueva Inscripción";
			body = "Un nuevo usuario se ha inscrito, pero no se proporcionó información específica del curso o evento.\n"
					+ "Correo electrónico del usuario: " + user.getEmail() + "\n";
		}

		emailService.sendEmail(adminEmail, subject, body);
	}

	public void sendEventOrCourseReminder(User user, Course course, Event event) {
		String subject;
		String body;

		System.out.println("Enviando recordatorio a: " + user.getEmail());

		if (course != null) {
			subject = "Recordatorio: Curso Próximo - " + course.getName();
			body = "Estimado " + user.getFirstName() + ",\n\n" + "Te recordamos que el curso: " + course.getName()
					+ " comenzará pronto.\n" + "Fecha de inicio: " + course.getStartDate() + "\n" + "Duración: "
					+ course.getDuration() + "\n\n" + "Gracias por ser parte de nuestra comunidad.\n";
		} else if (event != null) {
			subject = "Recordatorio: Evento Próximo - " + event.getName();
			body = "Estimado " + user.getFirstName() + ",\n\n" + "Te recordamos que el evento: " + event.getName()
					+ " se llevará a cabo pronto.\n" + "Fecha y hora: " + event.getStartDate() + "\n" + "Lugar: "
					+ event.getLocation() + "\n\n" + "Gracias por ser parte de nuestra comunidad.\n";
		} else {
			throw new IllegalArgumentException("Debe proporcionar un curso o evento para enviar el recordatorio.");
		}

		emailService.sendEmail(user.getEmail(), subject, body);
	}

	public void sendUpdateNotification(User user, Course course, Event event, String status, String comments,
			boolean statusChanged) {
		String subject = "Actualización en tu inscripción";
		StringBuilder body = new StringBuilder("Estimado " + user.getFirstName() + ",\n\n");

		if (course != null) {
			body.append("Tu inscripción en el curso \"" + course.getName() + "\" ha sido actualizada.\n");
		} else if (event != null) {
			body.append("Tu inscripción en el evento \"" + event.getName() + "\" ha sido actualizada.\n");
		} else {
			throw new IllegalArgumentException("La inscripción debe estar asociada a un curso o evento.");
		}

		if (statusChanged) {
			body.append("Nuevo estado de inscripción: " + status + "\n");
		}

		if (comments != null && !comments.isEmpty()) {
			body.append("\nComentario del administrador:\n" + comments);
		}

		emailService.sendEmail(user.getEmail(), subject, body.toString());
	}
}