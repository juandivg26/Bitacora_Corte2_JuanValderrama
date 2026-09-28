package com.restaurante.controller.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.restaurante.model.dto.response.PlatoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrato documentado de la vista de menu para el cliente.
 */
@Tag(name = "Menu", description = "Vista de solo lectura de la carta para el cliente (solo platos disponibles)")
public interface MenuApi {

    @Operation(summary = "Consultar el menu",
            description = "Devuelve unicamente los platos disponibles.")
    @ApiResponse(responseCode = "200", description = "Menu obtenido")
    ResponseEntity<List<PlatoResponseDTO>> verMenu();

    @Operation(summary = "Ver el detalle de un plato del menu",
            description = "Solo devuelve platos disponibles: si el plato no existe o esta desactivado responde 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plato del menu"),
            @ApiResponse(responseCode = "404", description = "El plato no existe o no esta disponible en el menu")
    })
    ResponseEntity<PlatoResponseDTO> verPlatoDelMenu(
            @Parameter(description = "ID del plato", example = "1") Long id);

    @Operation(summary = "Consultar el menu filtrado por categoria",
            description = "La comparacion de categoria ignora mayusculas y acentos.")
    @ApiResponse(responseCode = "200", description = "Menu obtenido")
    ResponseEntity<List<PlatoResponseDTO>> verMenuPorCategoria(
            @Parameter(description = "Categoria a filtrar", example = "PRINCIPALES") String categoria);
}
