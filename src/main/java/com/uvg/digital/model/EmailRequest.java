package com.uvg.digital.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
	
	 @NotBlank(message = "El nombre es obligatorio")
     private String name;

     @Email(message = "El correo electrónico no es válido")
     @NotBlank(message = "El correo electrónico es obligatorio")
     private String email;

     @NotBlank(message = "El mensaje es obligatorio")
     private String message;

}
