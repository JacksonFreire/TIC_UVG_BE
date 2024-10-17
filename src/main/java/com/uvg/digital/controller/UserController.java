package com.uvg.digital.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uvg.digital.entity.User;
import com.uvg.digital.model.EmailRequest;
import com.uvg.digital.model.UserDTO;
import com.uvg.digital.service.AzureEmailService;
import com.uvg.digital.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AzureEmailService emailService;

	
	@PostMapping("/register")
    public ResponseEntity<User> registerUser(@ModelAttribute UserDTO userDTO, @RequestPart("document") MultipartFile document) {
        User registeredUser = userService.registerUser(userDTO, document);
        return ResponseEntity.ok(registeredUser);
    }

	@GetMapping("/verify")
	public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
		boolean isVerified = userService.verifyUser(token);
		if (isVerified) {
			return ResponseEntity.ok("Email verificado exitosamente.");
		} else {
			return ResponseEntity.status(400).body("Token de verificación inválido o expirado.");
		}
	}
	
	@PostMapping("/send")
	public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest request) {

		String body = String.format("Nombre: %s\nEmail: %s\nMensaje: %s", request.getName(), request.getEmail(),
				request.getMessage());

		emailService.sendEmail("info@univeritasgroup.com", "Nuevo mensaje desde el formulario de contacto", body);

		return ResponseEntity.ok("Correo enviado correctamente");
	}

}
