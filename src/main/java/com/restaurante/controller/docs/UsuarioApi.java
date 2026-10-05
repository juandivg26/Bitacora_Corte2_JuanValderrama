package com.restaurante.controller.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.restaurante.model.dto.response.UsuarioResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Usuarios", description = "Administración de usuarios y roles del restaurante (Solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public interface UsuarioApi {

    @Operation(summary = "Listar todos los usuarios", description = "Solo accesible para rol ADMINISTRADOR.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos();

    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Permiso denegado")
    })
    ResponseEntity<UsuarioResponseDTO> obtenerPorId(
            @Parameter(description = "ID del usuario", example = "1") Long id);
}
