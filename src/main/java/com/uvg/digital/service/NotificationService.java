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
            body = "Estimado " + user.getFirstName() + ",\n\n" +
                   "Te has inscrito exitosamente en el curso: " + course.getName() + ".\n" +
                   "Fecha de inicio: " + course.getStartDate() + "\n" +
                   "Fecha de finalización: " + course.getEndDate() + "\n\n" +
                   "Gracias por ser parte de nuestra comunidad.\n";
        } else if (event != null) {
            subject = "Confirmación de Inscripción al Evento: " + event.getName();
            body = "Estimado " + user.getFirstName() + ",\n\n" +
                   "Te has inscrito exitosamente en el evento: " + event.getName() + ".\n" +
                   "Fecha del evento: " + event.getStartDate() + "\n" +
                   "Lugar: " + event.getLocation() + "\n\n" +
                   "Gracias por ser parte de nuestra comunidad.\n";
        } else {
            subject = "Confirmación de Inscripción";
            body = "Estimado " + user.getFirstName() + ",\n\n" +
                   "Tu inscripción ha sido procesada correctamente.\n\n" +
                   "Gracias por ser parte de nuestra comunidad.\n";
        }

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    public void notifyAdminOfNewEnrollment(Course course, Event event, User user) {
        String adminEmail = "admin@example.com"; // Cambia esto por la dirección de correo del administrador
        String subject;
        String body;

        if (course != null) {
            subject = "Nueva Inscripción al Curso: " + course.getName();
            body = "El usuario " + user.getFirstName() + " " + user.getLastName() + 
                   " se ha inscrito en el curso: " + course.getName() + ".\n" +
                   "Correo electrónico del usuario: " + user.getEmail() + "\n";
        } else if (event != null) {
            subject = "Nueva Inscripción al Evento: " + event.getName();
            body = "El usuario " + user.getFirstName() + " " + user.getLastName() + 
                   " se ha inscrito en el evento: " + event.getName() + ".\n" +
                   "Correo electrónico del usuario: " + user.getEmail() + "\n";
        } else {
            subject = "Nueva Inscripción";
            body = "Un nuevo usuario se ha inscrito, pero no se proporcionó información específica del curso o evento.\n" +
                   "Correo electrónico del usuario: " + user.getEmail() + "\n";
        }

        emailService.sendEmail(adminEmail, subject, body);
    }
}