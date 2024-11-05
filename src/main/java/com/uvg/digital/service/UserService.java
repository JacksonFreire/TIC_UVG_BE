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
    private final BlobStorageService blobStorageService;

    public User registerUser(UserDTO userDTO, MultipartFile document) {
    	
    	if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
    	
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

        // Subir la imagen a Azure Blob Storage y guardar la URL en la base de datos
        if (document != null && !document.isEmpty()) {
            try {
                String fileName = "user-document-" + UUID.randomUUID() + "-" + document.getOriginalFilename();
                String documentUrl = blobStorageService.uploadImage(document.getBytes(), "imagenes-usuarios", fileName);
                user.setDocumentUrl(documentUrl);
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
            user.setVerificationToken(null);
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
