package com.restaurante.controller.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrato documentado de la API de platos.
 * Toda la documentacion de Swagger vive aqui; el controller solo implementa.
 */
@Tag(name = "Platos", description = "Gestion de la carta del restaurante")
public interface PlatoApi {

    @Operation(summary = "Obtener todos los platos",
            description = "Devuelve la carta completa, incluidos los platos no disponibles.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<PlatoResponseDTO>> obtenerTodos();

    @Operation(summary = "Obtener platos disponibles")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<PlatoResponseDTO>> obtenerDisponibles();

    @Operation(summary = "Obtener platos por categoria",
            description = "La comparacion de categoria ignora mayusculas y acentos.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<PlatoResponseDTO>> obtenerPorCategoria(
            @Parameter(description = "Categoria a filtrar", example = "PRINCIPALES") String categoria);

    @Operation(summary = "Obtener plato por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un plato con ese id")
    })
    ResponseEntity<PlatoResponseDTO> obtenerPorId(
            @Parameter(description = "ID del plato", example = "1") Long id);

    @Operation(summary = "Crear un nuevo plato")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plato creado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un plato con ese nombre")
    })
    ResponseEntity<PlatoResponseDTO> crear(PlatoRequestDTO dto);

    @Operation(summary = "Actualizar un plato existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "404", description = "No existe un plato con ese id"),
            @ApiResponse(responseCode = "409", description = "Ya existe otro plato con ese nombre")
    })
    ResponseEntity<PlatoResponseDTO> actualizar(
            @Parameter(description = "ID del plato", example = "1") Long id,
            PlatoRequestDTO dto);

    @Operation(summary = "Cambiar disponibilidad de un plato")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada"),
            @ApiResponse(responseCode = "404", description = "No existe un plato con ese id")
    })
    ResponseEntity<PlatoResponseDTO> cambiarDisponibilidad(
            @Parameter(description = "ID del plato", example = "1") Long id,
            @Parameter(description = "true lo activa, false lo desactiva") boolean disponible);

    @Operation(summary = "Eliminar un plato",
            description = "No se permite eliminar un plato que tenga pedidos activos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Plato eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe un plato con ese id"),
            @ApiResponse(responseCode = "409", description = "El plato tiene pedidos activos")
    })
    ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del plato", example = "1") Long id);
}
