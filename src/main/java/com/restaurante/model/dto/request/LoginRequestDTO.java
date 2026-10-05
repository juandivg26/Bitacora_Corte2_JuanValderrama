package com.restaurante.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Credenciales para iniciar sesión")
public class LoginRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email no es válido")
    @Schema(example = "admin@americanbites.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(example = "Admin123*")
    private String password;
}
