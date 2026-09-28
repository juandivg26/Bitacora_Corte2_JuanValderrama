package com.restaurante.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.mapper.EventoPedidoMapper;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.dto.response.EventoPedidoResponseDTO;
import com.restaurante.service.IEventoPedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Eventos de pedido (MongoDB)", description = "Historial de cambios de estado de los pedidos, almacenado en MongoDB")
public class EventoPedidoController {

    private final IEventoPedidoService eventoPedidoService;
    private final EventoPedidoMapper eventoPedidoMapper;

    @GetMapping
    @Operation(summary = "Obtener todos los eventos de pedido")
    public ResponseEntity<List<EventoPedidoResponseDTO>> obtenerTodos() {
        log.info("GET /api/v1/eventos");
        List<EventoPedido> eventos = eventoPedidoService.obtenerTodos();
        return ResponseEntity.ok(eventoPedidoMapper.toResponseList(eventos));
    }

    @GetMapping("/pedido/{idPedido}")
    @Operation(summary = "Obtener los eventos de un pedido")
    public ResponseEntity<List<EventoPedidoResponseDTO>> obtenerPorPedido(@PathVariable UUID idPedido) {
        log.info("GET /api/v1/eventos/pedido/{}", idPedido);
        List<EventoPedido> eventos = eventoPedidoService.obtenerPorPedido(idPedido);
        return ResponseEntity.ok(eventoPedidoMapper.toResponseList(eventos));
    }
}
