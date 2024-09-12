package com.uvg.digital.security;

import com.uvg.digital.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Ejecutando JwtAuthenticationFilter para la solicitud: " + request.getRequestURI());
        
        String jwt = parseJwt(request);
        if (jwt != null && jwtTokenService.validateToken(jwt)) {
            System.out.println("Token JWT validado correctamente.");

            String username = jwtTokenService.getUsernameFromToken(jwt);
            System.out.println("Usuario extraído del token: " + username);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("Detalles del usuario cargados: " + userDetails);
            
            if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("Cargando detalles del usuario: " + username);
                System.out.println("Roles del usuario: " + userDetails.getAuthorities());
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Autenticación establecida para el usuario: " + username);
            } else {
                System.out.println("No se pudo establecer la autenticación para el usuario: " + username);
            }
        } else {
            System.out.println("Token JWT no válido o no presente");
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

}