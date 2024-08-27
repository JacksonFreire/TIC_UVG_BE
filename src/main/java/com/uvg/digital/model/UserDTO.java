package com.uvg.digital.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String password;
	private String phoneNumber;
	private String role;
	private String birthDate;
	private Boolean verified;
}
