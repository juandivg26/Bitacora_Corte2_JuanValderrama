package com.restaurante.controller.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrato documentado de la API de mesas.
 */
@Tag(name = "Mesas", description = "Gestion de las mesas del restaurante")
public interface MesaApi {

    @Operation(summary = "Obtener todas las mesas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<MesaResponseDTO>> obtenerTodas();

    @Operation(summary = "Obtener mesas disponibles",
            description = "Solo las mesas en estado DISPONIBLE.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<MesaResponseDTO>> obtenerDisponibles();

    @Operation(summary = "Obtener mesa por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mesa encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una mesa con ese id")
    })
    ResponseEntity<MesaResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la mesa", example = "1") Long id);

    @Operation(summary = "Crear una nueva mesa",
            description = "Las mesas se crean siempre en estado DISPONIBLE y con cuentaAbierta en false.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Mesa creada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe una mesa con ese numero")
    })
    ResponseEntity<MesaResponseDTO> crear(MesaRequestDTO dto);

    @Operation(summary = "Cambiar el estado de una mesa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "No existe una mesa con ese id"),
            @ApiResponse(responseCode = "422", description = "Transicion de estado invalida")
    })
    ResponseEntity<MesaResponseDTO> cambiarEstado(
            @Parameter(description = "ID de la mesa", example = "1") Long id,
            @Parameter(description = "Nuevo estado de la mesa") EstadoMesa nuevoEstado);

    @Operation(summary = "Eliminar una mesa")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Mesa eliminada"),
            @ApiResponse(responseCode = "404", description = "No existe una mesa con ese id")
    })
    ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la mesa", example = "1") Long id);
}
