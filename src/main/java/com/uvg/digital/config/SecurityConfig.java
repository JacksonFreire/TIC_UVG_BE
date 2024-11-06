package com.uvg.digital.config;

import com.uvg.digital.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthFilter;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**")
				.permitAll()

				// Endpoints públicos
				.requestMatchers("/api/users/register", "/api/users/verify**", "/api/users/send", "/api/auth/**","/api/activities/courses/list","/api/activities/courses/details/*","/api/activities/events","/api/activities/events/*")
				.permitAll()

				// Endpoints protegidos para administradores con rol ADMIN
				.requestMatchers("/api/activities/instructor/list","/api/enrollments/admin/course/*", "/api/enrollments/admin/event/*",
						"/api/activities/courses/create", "/api/activities/courses/update/*",
						"/api/activities/courses/delete/*", "/api/activities/events/create",
						"/api/activities/events/update/*", "/api/activities/events/delete/*")
				.hasAuthority("ADMIN")

				// Endpoints protegidos para usuarios con rol USER
				.requestMatchers("/api/enrollments/isEnrolled", "/api/enrollments/course/*", "/api/enrollments/event/*")
				.hasAuthority("USER")

				.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
