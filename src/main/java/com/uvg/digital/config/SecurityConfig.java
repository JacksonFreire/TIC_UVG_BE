package com.uvg.digital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Desactiva CSRF
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/users/register", "/api/users/verify**").permitAll() // Permite
																										// acceso sin
																										// autenticación
						.anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud
				);
		return http.build();
	}
	
	/*
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Permite todas las
																							// solicitudes temporalmente
		return http.build();
	}*/

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
