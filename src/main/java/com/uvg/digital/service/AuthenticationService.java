package com.uvg.digital.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uvg.digital.entity.User;
import com.uvg.digital.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String authenticate(String username, String password) {
        
    	 // Aquí comparas la contraseña proporcionada con la contraseña codificada
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("Passwords match: " + matches); // Este log te dirá si coinciden o no
    	
    	
    	Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));
        return jwtTokenService.generateToken(authentication);
    }
}