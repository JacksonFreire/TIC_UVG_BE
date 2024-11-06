package com.uvg.digital.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.base.url.vue}")
    private String baseUrlFe;

    @Value("${app.base.url.aruba}")
    private String arubaBaseUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Permitir solo las URLs que empiezan con /api
                .allowedOrigins(baseUrlFe, arubaBaseUrl)  // Permitir solo estos dos orígenes
                .allowedMethods("GET", "POST", "PUT", "DELETE","OPTIONS")  // Permitir estos métodos
                .allowedHeaders("Authorization", "Content-Type")  // Solo permitir ciertos encabezados
                .allowCredentials(true);  // Permitir credenciales si es necesario
    }
}
