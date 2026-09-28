package com.restaurante.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.controller.docs.EventoPedidoApi;
import com.restaurante.mapper.EventoPedidoMapper;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.dto.response.EventoPedidoResponseDTO;
import com.restaurante.service.IEventoPedidoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/eventos")
@RequiredArgsConstructor
@Slf4j
public class EventoPedidoController implements EventoPedidoApi {

    private final IEventoPedidoService eventoPedidoService;
    private final EventoPedidoMapper eventoPedidoMapper;

    @Override
    @GetMapping
    public ResponseEntity<List<EventoPedidoResponseDTO>> obtenerTodos() {
        log.info("GET /api/v1/eventos");
        List<EventoPedido> eventos = eventoPedidoService.obtenerTodos();
        return ResponseEntity.ok(eventoPedidoMapper.toResponseList(eventos));
    }

    @Override
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<EventoPedidoResponseDTO>> obtenerPorPedido(@PathVariable UUID idPedido) {
        log.info("GET /api/v1/eventos/pedido/{}", idPedido);
        List<EventoPedido> eventos = eventoPedidoService.obtenerPorPedido(idPedido);
        return ResponseEntity.ok(eventoPedidoMapper.toResponseList(eventos));
    }
}
