package com.restaurante.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta de autenticación exitosa con Token JWT")
public class TokenResponseDTO {

    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    @Builder.Default
    private String tipo = "Bearer";

    @Schema(description = "Email del usuario autenticado", example = "admin@americanbites.com")
    private String email;

    @Schema(description = "Rol del usuario autenticado", example = "ADMINISTRADOR")
    private String rol;

    @Schema(description = "Tiempo de validez en milisegundos", example = "3600000")
    private Long expiraEnMs;

    public TokenResponseDTO(String token) {
        this.token = token;
        this.tipo = "Bearer";
    }
}
