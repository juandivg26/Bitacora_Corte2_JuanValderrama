package com.restaurante.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.mapper.MesaMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;
import com.restaurante.service.IMesaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/mesas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mesas", description = "Gestión de las mesas del restaurante")
public class MesaController {

    private final IMesaService mesaService;
    private final MesaMapper mesaMapper;

    @GetMapping
    @Operation(summary = "Obtener todas las mesas")
    public ResponseEntity<List<MesaResponseDTO>> obtenerTodas() {
        log.info("GET /api/v1/mesas");
        List<Mesa> mesas = mesaService.obtenerTodas();
        return ResponseEntity.ok(mesaMapper.toResponseList(mesas));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Obtener mesas disponibles")
    public ResponseEntity<List<MesaResponseDTO>> obtenerDisponibles() {
        List<Mesa> mesas = mesaService.obtenerDisponibles();
        return ResponseEntity.ok(mesaMapper.toResponseList(mesas));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mesa por ID")
    public ResponseEntity<MesaResponseDTO> obtenerPorId(@PathVariable Long id) {
        Mesa mesa = mesaService.obtenerPorId(id);
        return ResponseEntity.ok(mesaMapper.toResponse(mesa));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva mesa")
    public ResponseEntity<MesaResponseDTO> crear(@RequestBody @Valid MesaRequestDTO dto) {
        log.info("POST /api/v1/mesas - numero={}", dto.getNumero());
        Mesa mesa = mesaMapper.toDomain(dto);
        Mesa creada = mesaService.crear(mesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaMapper.toResponse(creada));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de una mesa")
    public ResponseEntity<MesaResponseDTO> cambiarEstado(@PathVariable Long id,
                                                          @RequestParam EstadoMesa nuevoEstado) {
        Mesa actualizada = mesaService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(mesaMapper.toResponse(actualizada));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una mesa")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mesaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
