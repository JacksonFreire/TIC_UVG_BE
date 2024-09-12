package com.uvg.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstructorDTO {

	private Long id;
	private String name;
	private String bio;
	private String profileImage;

}
