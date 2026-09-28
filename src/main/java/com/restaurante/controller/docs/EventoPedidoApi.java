package com.restaurante.controller.docs;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.restaurante.model.dto.response.EventoPedidoResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrato documentado de la API del log de eventos (persistencia NoSQL).
 */
@Tag(name = "Eventos de pedido (MongoDB)",
        description = "Historial de cambios de estado de los pedidos, almacenado en MongoDB")
public interface EventoPedidoApi {

    @Operation(summary = "Obtener todos los eventos de pedido",
            description = "Devuelve el log completo del mas reciente al mas antiguo.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<EventoPedidoResponseDTO>> obtenerTodos();

    @Operation(summary = "Obtener los eventos de un pedido",
            description = "Devuelve el historial de un pedido, del mas reciente al mas antiguo.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido")
    ResponseEntity<List<EventoPedidoResponseDTO>> obtenerPorPedido(
            @Parameter(description = "UUID v7 del pedido") UUID idPedido);
}
