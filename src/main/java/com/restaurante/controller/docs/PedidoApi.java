package com.restaurante.controller.docs;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.restaurante.model.dto.request.CambiarEstadoPedidoRequestDTO;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrato documentado de la API de pedidos.
 * Los pedidos se identifican con UUID v7.
 */
@Tag(name = "Pedidos", description = "Gestion de los pedidos de las mesas")
public interface PedidoApi {

    @Operation(summary = "Obtener todos los pedidos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<PedidoResponseDTO>> obtenerTodos();

    @Operation(summary = "Obtener los pedidos de una mesa")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<PedidoResponseDTO>> obtenerPorMesa(
            @Parameter(description = "ID de la mesa", example = "1") Long idMesa);

    @Operation(summary = "Obtener pedido por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un pedido con ese id")
    })
    ResponseEntity<PedidoResponseDTO> obtenerPorId(
            @Parameter(description = "UUID v7 del pedido") UUID id);

    @Operation(summary = "Crear un nuevo pedido",
            description = "Congela el precio de cada item y deja la mesa en OCUPADA. "
                    + "Registra el evento de creacion en MongoDB (no critico).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "404", description = "La mesa o el plato no existen"),
            @ApiResponse(responseCode = "422", description = "La mesa no esta disponible o un plato no esta disponible")
    })
    ResponseEntity<PedidoResponseDTO> crear(PedidoRequestDTO dto);

    @Operation(summary = "Agregar un item a un pedido existente",
            description = "Solo permitido mientras el pedido esta en estado RECIBIDO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item agregado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "404", description = "El pedido o el plato no existen"),
            @ApiResponse(responseCode = "422", description = "El pedido no puede modificarse o el plato no esta disponible")
    })
    ResponseEntity<PedidoResponseDTO> agregarItem(
            @Parameter(description = "UUID v7 del pedido") UUID id,
            ItemPedidoRequestDTO dto);

    @Operation(summary = "Cambiar el estado de un pedido",
            description = "Registra el evento de cambio de estado en MongoDB (no critico).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos"),
            @ApiResponse(responseCode = "404", description = "No existe un pedido con ese id"),
            @ApiResponse(responseCode = "422", description = "Transicion de estado invalida")
    })
    ResponseEntity<PedidoResponseDTO> cambiarEstado(
            @Parameter(description = "UUID v7 del pedido") UUID id,
            CambiarEstadoPedidoRequestDTO dto);
}
