package com.restaurante.controller.docs;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.restaurante.model.dto.request.CuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrato documentado de la API de cuentas.
 */
@Tag(name = "Cuentas", description = "Gestion del cobro y cierre de cuentas por mesa")
public interface CuentaApi {

    @Operation(summary = "Obtener todas las cuentas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<CuentaResponseDTO>> obtenerTodas();

    @Operation(summary = "Obtener cuenta por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una cuenta con ese id")
    })
    ResponseEntity<CuentaResponseDTO> obtenerPorId(
            @Parameter(description = "ID de la cuenta", example = "1") Long id);

    @Operation(summary = "Obtener la cuenta abierta de una mesa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta abierta de la mesa"),
            @ApiResponse(responseCode = "404", description = "La mesa no tiene una cuenta abierta")
    })
    ResponseEntity<CuentaResponseDTO> obtenerPorMesa(
            @Parameter(description = "ID de la mesa", example = "1") Long idMesa);

    @Operation(summary = "Abrir la cuenta de una mesa",
            description = "Marca la mesa como OCUPADA con cuentaAbierta en true. "
                    + "Una mesa solo puede tener una cuenta no cerrada a la vez.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cuenta abierta"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "404", description = "No existe una mesa con ese id"),
            @ApiResponse(responseCode = "422", description = "La mesa ya tiene una cuenta abierta")
    })
    ResponseEntity<CuentaResponseDTO> abrir(CuentaRequestDTO dto);

    @Operation(summary = "Registrar el pago de una cuenta",
            description = "Calcula el total sumando los items de los pedidos NO cancelados de la mesa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago registrado"),
            @ApiResponse(responseCode = "404", description = "No existe una cuenta con ese id"),
            @ApiResponse(responseCode = "422", description = "Transicion de estado invalida")
    })
    ResponseEntity<CuentaResponseDTO> registrarPago(
            @Parameter(description = "ID de la cuenta", example = "1") Long id);

    @Operation(summary = "Cerrar una cuenta",
            description = "Deja la mesa DISPONIBLE y con cuentaAbierta en false.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta cerrada"),
            @ApiResponse(responseCode = "404", description = "No existe una cuenta con ese id"),
            @ApiResponse(responseCode = "422", description = "Transicion de estado invalida")
    })
    ResponseEntity<CuentaResponseDTO> cerrar(
            @Parameter(description = "ID de la cuenta", example = "1") Long id);
}
