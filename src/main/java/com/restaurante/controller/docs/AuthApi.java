package com.restaurante.controller.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.RegistroUsuarioRequestDTO;
import com.restaurante.model.dto.response.TokenResponseDTO;
import com.restaurante.model.dto.response.UsuarioResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticación", description = "Endpoints para registro, inicio de sesión y gestión de tokens JWT")
public interface AuthApi {

    @Operation(summary = "Iniciar sesión", description = "Autentica con email y contraseña, retornando un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    ResponseEntity<TokenResponseDTO> login(LoginRequestDTO dto);

    @Operation(summary = "Registrar nuevo usuario", description = "Crea un usuario nuevo con contraseña encriptada (BCrypt).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    ResponseEntity<UsuarioResponseDTO> registrar(RegistroUsuarioRequestDTO dto);

    @Operation(summary = "Consultar usuario actual", description = "Retorna el perfil del usuario autenticado a partir del token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil obtenido"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    ResponseEntity<UsuarioResponseDTO> me(Authentication authentication);
}
