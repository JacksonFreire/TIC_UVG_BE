package com.uvg.digital.service;


import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uvg.digital.entity.PasswordResetToken;
import com.uvg.digital.entity.User;
import com.uvg.digital.repository.PasswordResetTokenRepository;
import com.uvg.digital.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.base.url}")
    private String baseUrl;

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(null, token, user, LocalDateTime.now().plusHours(24));
        tokenRepository.save(resetToken);
        String resetLink = baseUrl + "/api/auth/reset-password?token=" + token;
        emailService.sendEmail(email, "Password Reset Request", "Click the link to reset your password: " + resetLink);
    }
    

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        User user = resetToken.getUser();
        String encodedPassword = passwordEncoder.encode(newPassword);
        System.out.println("Nueva contraseña codificada: " + encodedPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    } 
    
    public boolean authenticateUser(String rawPassword, String encodedPassword) {
        boolean match = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("Passwords match: " + match);
        return match;
    }
}