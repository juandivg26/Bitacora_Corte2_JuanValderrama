package com.restaurante.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("American Bites - API Restaurante (DOSW)")
                        .description("API REST del restaurante de comida rápida. "
                                + "Persistencia híbrida: PostgreSQL (Spring Data JPA) para el núcleo "
                                + "y MongoDB para el log de eventos de pedidos. Bitácora Corte 2 (S7-S9).")
                        .version("v1.0"));
    }
}
