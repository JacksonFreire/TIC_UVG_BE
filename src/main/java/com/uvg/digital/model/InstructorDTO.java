package com.uvg.digital.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstructorDTO {

	private Long id;
	private String name; // Nombre del instructor
	private String bio; // Biografía
	private String profileImage; // Imagen de perfil si es necesario

}
