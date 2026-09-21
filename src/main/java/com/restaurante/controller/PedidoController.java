package com.restaurante.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.mapper.ItemPedidoMapper;
import com.restaurante.mapper.PedidoMapper;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.CambiarEstadoPedidoRequestDTO;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;
import com.restaurante.service.IPedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Pedidos", description = "Gestión de los pedidos de las mesas")
public class PedidoController {

    private final IPedidoService pedidoService;
    private final PedidoMapper pedidoMapper;
    private final ItemPedidoMapper itemPedidoMapper;

    @GetMapping
    @Operation(summary = "Obtener todos los pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerTodos() {
        log.info("GET /api/v1/pedidos");
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        return ResponseEntity.ok(pedidoMapper.toResponseList(pedidos));
    }

    @GetMapping("/mesa/{idMesa}")
    @Operation(summary = "Obtener pedidos de una mesa")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPorMesa(@PathVariable Long idMesa) {
        List<Pedido> pedidos = pedidoService.obtenerPorMesa(idMesa);
        return ResponseEntity.ok(pedidoMapper.toResponseList(pedidos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID")
    public ResponseEntity<PedidoResponseDTO> obtenerPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.obtenerPorId(id);
        return ResponseEntity.ok(pedidoMapper.toResponse(pedido));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido")
    public ResponseEntity<PedidoResponseDTO> crear(@RequestBody @Valid PedidoRequestDTO dto) {
        log.info("POST /api/v1/pedidos - idMesa={}", dto.getIdMesa());
        Pedido pedido = pedidoMapper.toDomain(dto);
        Pedido creado = pedidoService.crear(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toResponse(creado));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Agregar un ítem a un pedido existente")
    public ResponseEntity<PedidoResponseDTO> agregarItem(@PathVariable Long id,
                                                          @RequestBody @Valid ItemPedidoRequestDTO dto) {
        ItemPedido item = itemPedidoMapper.toDomain(dto);
        Pedido actualizado = pedidoService.agregarItem(id, item);
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de un pedido")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable Long id,
                                                            @RequestBody @Valid CambiarEstadoPedidoRequestDTO dto) {
        Pedido actualizado = pedidoService.cambiarEstado(id, dto.getEstado());
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }
}
