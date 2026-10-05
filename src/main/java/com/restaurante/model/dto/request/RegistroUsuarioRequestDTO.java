package com.restaurante.model.dto.request;

import com.restaurante.model.domain.Rol;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos requeridos para registrar un usuario")
public class RegistroUsuarioRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email no es válido")
    @Schema(example = "cliente@americanbites.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(example = "Cliente123*")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Schema(example = "Carlos Gómez")
    private String nombre;

    @Schema(description = "Rol asignado (por defecto CLIENTE si no se especifica)", example = "CLIENTE")
    private Rol rol;
}
