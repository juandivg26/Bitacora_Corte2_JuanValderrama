package com.restaurante.model.dto.response;

import java.time.LocalDateTime;

import com.restaurante.model.domain.Rol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Información del usuario")
public class UsuarioResponseDTO {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "admin@americanbites.com")
    private String email;

    @Schema(example = "Administrador General")
    private String nombre;

    @Schema(example = "ADMINISTRADOR")
    private Rol rol;

    @Schema(example = "2026-10-05T10:00:00")
    private LocalDateTime fechaCreacion;
}
