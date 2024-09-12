package com.uvg.digital.service;

import static org.mockito.Mockito.*;

import com.uvg.digital.entity.Course;
import com.uvg.digital.entity.Enrollment;
import com.uvg.digital.entity.Event;
import com.uvg.digital.entity.User;
import com.uvg.digital.repository.CourseRepository;
import com.uvg.digital.repository.EnrollmentRepository;
import com.uvg.digital.repository.EventRepository;
import com.uvg.digital.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    void testEnrollUserToCourse() {
        User user = new User();
        user.setId(1L);

        Course course = new Course();
        course.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByUserAndCourse(user, course)).thenReturn(false);

        enrollmentService.enrollUserToCourse(1L, 1L);

        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
        verify(notificationService, times(1)).sendEnrollmentConfirmation(user, course, null);
        verify(notificationService, times(1)).notifyAdminOfNewEnrollment(course, null, user);
    }

    @Test
    void testEnrollUserToEvent() {
        User user = new User();
        user.setId(1L);

        Event event = new Event();
        event.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(enrollmentRepository.existsByUserAndEvent(user, event)).thenReturn(false);

        enrollmentService.enrollUserToEvent(1L, 1L);

        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
        verify(notificationService, times(1)).sendEnrollmentConfirmation(user, null, event);
        verify(notificationService, times(1)).notifyAdminOfNewEnrollment(null, event, user);
    }
}
