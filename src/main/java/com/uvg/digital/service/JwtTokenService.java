package com.uvg.digital.service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.uvg.digital.entity.User;
import com.uvg.digital.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenService {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;
    
    @Autowired
    private UserRepository userRepository;

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        
        String userId = String.valueOf(user.get().getId());
        String username = userDetails.getUsername();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
        
        String roles = userDetails.getAuthorities().stream()
        	    .map(GrantedAuthority::getAuthority)
        	    .collect(Collectors.joining(","));
        
        System.out.println("Roles incluidos en el token: " + roles);

        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)
            .claim("role", roles)
            .setIssuedAt(currentDate)
            .setExpiration(expireDate)
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token);
            System.out.println("Token JWT válido.");
            return true;
        } catch (Exception e) {
            System.out.println("Error durante la validación del token JWT: " + e.getMessage());
            return false;
        }
    }
}
