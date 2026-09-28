package com.restaurante.controller.docs;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.restaurante.model.dto.request.ReprogramarReservaRequestDTO;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrato documentado de la API de reservas.
 * Las reservas se identifican con UUID v7.
 */
@Tag(name = "Reservas", description = "Gestion de reservas de mesas")
public interface ReservaApi {

    @Operation(summary = "Obtener todas las reservas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<ReservaResponseDTO>> obtenerTodas();

    @Operation(summary = "Obtener las reservas de una mesa")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<ReservaResponseDTO>> obtenerPorMesa(
            @Parameter(description = "ID de la mesa", example = "1") Long idMesa);

    @Operation(summary = "Obtener reserva por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una reserva con ese id")
    })
    ResponseEntity<ReservaResponseDTO> obtenerPorId(
            @Parameter(description = "UUID v7 de la reserva") UUID id);

    @Operation(summary = "Crear una nueva reserva",
            description = "La mesa debe estar DISPONIBLE y la fecha debe ser futura. "
                    + "Al crearla, la mesa pasa a RESERVADA.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "404", description = "No existe una mesa con ese id"),
            @ApiResponse(responseCode = "422", description = "La mesa no esta disponible, la fecha no es futura o se solapa con otra reserva")
    })
    ResponseEntity<ReservaResponseDTO> crear(ReservaRequestDTO dto);

    @Operation(summary = "Cancelar una reserva",
            description = "Si la mesa estaba RESERVADA, vuelve a DISPONIBLE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "404", description = "No existe una reserva con ese id"),
            @ApiResponse(responseCode = "422", description = "La reserva ya estaba cancelada")
    })
    ResponseEntity<ReservaResponseDTO> cancelar(
            @Parameter(description = "UUID v7 de la reserva") UUID id);

    @Operation(summary = "Reprogramar una reserva",
            description = "Una reserva cancelada no puede reprogramarse.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva reprogramada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "404", description = "No existe una reserva con ese id"),
            @ApiResponse(responseCode = "422", description = "La reserva esta cancelada o la nueva fecha no es futura")
    })
    ResponseEntity<ReservaResponseDTO> reprogramar(
            @Parameter(description = "UUID v7 de la reserva") UUID id,
            ReprogramarReservaRequestDTO dto);
}
