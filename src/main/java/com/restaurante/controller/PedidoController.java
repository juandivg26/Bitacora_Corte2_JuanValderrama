package com.restaurante.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.controller.docs.PedidoApi;
import com.restaurante.mapper.ItemPedidoMapper;
import com.restaurante.mapper.PedidoMapper;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.CambiarEstadoPedidoRequestDTO;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.IPedidoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Slf4j
public class PedidoController implements PedidoApi {

    private final IPedidoService pedidoService;
    private final PedidoMapper pedidoMapper;
    private final ItemPedidoMapper itemPedidoMapper;

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO', 'COCINERO', 'CHEF', 'CAJERO')")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerTodos() {
        log.info("GET /api/v1/pedidos");
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        return ResponseEntity.ok(pedidoMapper.toResponseList(pedidos));
    }

    @Override
    @GetMapping("/mesa/{idMesa}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO', 'COCINERO', 'CHEF', 'CAJERO')")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPorMesa(@PathVariable Long idMesa) {
        List<Pedido> pedidos = pedidoService.obtenerPorMesa(idMesa);
        return ResponseEntity.ok(pedidoMapper.toResponseList(pedidos));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO', 'COCINERO', 'CHEF', 'CAJERO')")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable UUID id) {
        Pedido pedido = pedidoService.obtenerPorId(id);
        return ResponseEntity.ok(pedidoMapper.toResponse(pedido));
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO')")
    public ResponseEntity<PedidoResponseDTO> crear(@RequestBody @Valid PedidoRequestDTO dto) {
        log.info("POST /api/v1/pedidos - idMesa={}", dto.getIdMesa());
        Pedido pedido = pedidoMapper.toDomain(dto);
        Pedido creado = pedidoService.crear(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toResponse(creado));
    }

    @Override
    @PostMapping("/{id}/items")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO')")
    public ResponseEntity<PedidoResponseDTO> agregarItem(@PathVariable UUID id,
                                                         @RequestBody @Valid ItemPedidoRequestDTO dto) {
        ItemPedido item = itemPedidoMapper.toDomain(dto);
        Pedido actualizado = pedidoService.agregarItem(id, item);
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }

    @Override
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO', 'COCINERO', 'CHEF')")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable UUID id,
                                                           @RequestBody @Valid CambiarEstadoPedidoRequestDTO dto) {
        Pedido actualizado = pedidoService.cambiarEstado(id, dto.getEstado());
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }
}
