package com.restaurante.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.controller.docs.ReservaApi;
import com.restaurante.mapper.ReservaMapper;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReprogramarReservaRequestDTO;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;
import com.restaurante.service.IReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Slf4j
public class ReservaController implements ReservaApi {

    private final IReservaService reservaService;
    private final ReservaMapper reservaMapper;

    @Override
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodas() {
        log.info("GET /api/v1/reservas");
        List<Reserva> reservas = reservaService.obtenerTodas();
        return ResponseEntity.ok(reservaMapper.toResponseList(reservas));
    }

    @Override
    @GetMapping("/mesa/{idMesa}")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerPorMesa(@PathVariable Long idMesa) {
        List<Reserva> reservas = reservaService.obtenerPorMesa(idMesa);
        return ResponseEntity.ok(reservaMapper.toResponseList(reservas));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerPorId(@PathVariable UUID id) {
        Reserva reserva = reservaService.obtenerPorId(id);
        return ResponseEntity.ok(reservaMapper.toResponse(reserva));
    }

    @Override
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@RequestBody @Valid ReservaRequestDTO dto) {
        log.info("POST /api/v1/reservas - idMesa={}, cliente={}", dto.getIdMesa(), dto.getCliente());
        Reserva reserva = reservaMapper.toDomain(dto);
        Reserva creada = reservaService.crear(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaMapper.toResponse(creada));
    }

    @Override
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable UUID id) {
        Reserva reserva = reservaService.cancelar(id);
        return ResponseEntity.ok(reservaMapper.toResponse(reserva));
    }

    @Override
    @PatchMapping("/{id}/reprogramar")
    public ResponseEntity<ReservaResponseDTO> reprogramar(@PathVariable UUID id,
                                                          @RequestBody @Valid ReprogramarReservaRequestDTO dto) {
        Reserva reserva = reservaService.reprogramar(id, dto.getFechaHora());
        return ResponseEntity.ok(reservaMapper.toResponse(reserva));
    }
}
