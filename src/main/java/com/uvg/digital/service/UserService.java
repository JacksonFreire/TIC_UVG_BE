package com.uvg.digital.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uvg.digital.entity.User;
import com.uvg.digital.model.UserDTO;
import com.uvg.digital.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	@Value("${app.base.url}")
	private String baseUrl;

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AzureEmailService emailService;

	public User registerUser(UserDTO userDTO, MultipartFile document) {
		User user = new User();
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setUsername(userDTO.getUsername());
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user.setPhoneNumber(userDTO.getPhoneNumber());
		user.setRole(userDTO.getRole());
		user.setBirthDate(LocalDate.parse(userDTO.getBirthDate()));
		user.setVerified(userDTO.getVerified());

		// Convertir y guardar el archivo como byte[]
		if (document != null && !document.isEmpty()) {
			try {
				user.setDocument(document.getBytes());
			} catch (IOException e) {
				throw new RuntimeException("Error al procesar el documento", e);
			}
		}

		String token = UUID.randomUUID().toString();
		user.setVerificationToken(token);
		User savedUser = userRepository.save(user);
		sendVerificationEmail(savedUser, token);
		return savedUser;
	}

	public boolean verifyUser(String token) {
		Optional<User> userOpt = userRepository.findByVerificationToken(token);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			user.setVerified(true);
			user.setVerificationToken(null); // Limpiamos el token
			userRepository.save(user);
			return true;
		}
		return false;
	}

	private void sendVerificationEmail(User user, String token) {
		String subject = "Verificación de correo electrónico";
		String verificationUrl = baseUrl + "/api/users/verify?token=" + token;
		String body = "Por favor, verifica tu correo electrónico haciendo clic en el siguiente enlace: "
				+ verificationUrl;
		emailService.sendEmail(user.getEmail(), subject, body);
	}
}
