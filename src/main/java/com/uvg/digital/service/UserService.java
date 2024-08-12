package com.uvg.digital.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uvg.digital.entity.User;
import com.uvg.digital.repository.UserRepository;

@Service
public class UserService {
	
	@Value("${app.base.url}")
    private String baseUrl;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService; // Inyección de EmailService

	public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
        String body = "Por favor, verifica tu correo electrónico haciendo clic en el siguiente enlace: " + verificationUrl;
        emailService.sendEmail(user.getEmail(), subject, body);
    }
}
