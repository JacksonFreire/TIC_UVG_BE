package com.uvg.digital.model;

import lombok.Data;

@Data
public class UserEnrollmentDTO {
	private Long userId;
	private Long courseId;
	private Long eventId;
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String phoneNumber;
	private String birthDate;
	private String status;
	private String comments;
}
