package com.uvg.digital.model;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UserRegistrationDTO {
	
	private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
    private LocalDate birthDate;
    private String role;
    private Boolean verified;
    private MultipartFile document;
}
