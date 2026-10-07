package com.restaurante.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Orígenes permitidos para desarrollo y producción
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",    // React
                "http://localhost:5173",    // Vite
                "http://localhost:4200",    // Angular
                "http://localhost:8080",    // API local
                "https://*.americanbites.com",  // Producción
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:4200"
        ));
        
        // Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        
        // Headers permitidos
        config.setAllowedHeaders(List.of("*"));
        
        // Headers expuestos al cliente
        config.setExposedHeaders(List.of("Authorization", "Content-Type", "X-Total-Count"));
        
        // Permitir credenciales (true solo si confías en los orígenes)
        config.setAllowCredentials(false);
        
        // Tiempo de caché para preflight
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}