package com.restaurante.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("American Bites — API REST del Restaurante")
                        .description("API REST para la gestión operativa del restaurante American Bites. "
                                + "Implementa arquitectura por capas, persistencia híbrida (PostgreSQL + MongoDB), "
                                + "y seguridad integral basada en Spring Security 6, JWT, HTTP Basic y OAuth2 (Google).")
                        .version("v2.0 (S10 - Seguridad)")
                        .contact(new Contact()
                                .name("Juan Diego Valderrama Gaviria")
                                .email("juan.valderrama-g@mail.escuelaing.edu.co"))
                        .license(new License()
                                .name("Uso Académico DOSW - Escuela Colombiana de Ingeniería Julio Garavito")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth").addList("basicAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el token JWT obtenido en /api/v1/auth/login. Ejemplo: eyJhbGciOi..."))
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .name("basicAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")
                                        .description("Autenticación HTTP Basic con email y password")));
    }
}
