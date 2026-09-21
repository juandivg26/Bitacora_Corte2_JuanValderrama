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

import com.restaurante.mapper.ReservaMapper;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReprogramarReservaRequestDTO;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.service.IReservaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservas", description = "Gestión de reservas de mesas")
public class ReservaController {

    private final IReservaService reservaService;
    private final ReservaMapper reservaMapper;

    @GetMapping
    @Operation(summary = "Obtener todas las reservas")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodas() {
        log.info("GET /api/v1/reservas");
        List<Reserva> reservas = reservaService.obtenerTodas();
        return ResponseEntity.ok(reservaMapper.toResponseList(reservas));
    }

    @GetMapping("/mesa/{idMesa}")
    @Operation(summary = "Obtener reservas de una mesa")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorMesa(@PathVariable Long idMesa) {
        List<Reserva> reservas = reservaService.obtenerPorMesa(idMesa);
        return ResponseEntity.ok(reservaMapper.toResponseList(reservas));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable Long id) {
        Reserva reserva = reservaService.obtenerPorId(id);
        return ResponseEntity.ok(reservaMapper.toResponse(reserva));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<ReservaResponseDTO> crear(@RequestBody @Valid ReservaRequestDTO dto) {
        log.info("POST /api/v1/reservas - idMesa={}, cliente={}", dto.getIdMesa(), dto.getCliente());
        Reserva reserva = reservaMapper.toDomain(dto);
        Reserva creada = reservaService.crear(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaMapper.toResponse(creada));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        Reserva reserva = reservaService.cancelar(id);
        return ResponseEntity.ok(reservaMapper.toResponse(reserva));
    }

    @PatchMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar una reserva")
    public ResponseEntity<ReservaResponseDTO> reprogramar(@PathVariable Long id,
                                                           @RequestBody @Valid ReprogramarReservaRequestDTO dto) {
        Reserva reserva = reservaService.reprogramar(id, dto.getFechaHora());
        return ResponseEntity.ok(reservaMapper.toResponse(reserva));
    }
}
