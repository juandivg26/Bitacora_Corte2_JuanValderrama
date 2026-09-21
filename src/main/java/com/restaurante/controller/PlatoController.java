package com.restaurante.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.mapper.PlatoMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.IPlatoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Platos", description = "Gestión de la carta del restaurante")
public class PlatoController {

    private final IPlatoService platoService;
    private final PlatoMapper platoMapper;

    @GetMapping
    @Operation(summary = "Obtener todos los platos")
    public ResponseEntity<List<PlatoResponseDTO>> obtenerTodos() {
        log.info("GET /api/v1/platos");
        List<Plato> platos = platoService.obtenerTodos();
        return ResponseEntity.ok(platoMapper.toResponseList(platos));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Obtener platos disponibles")
    public ResponseEntity<List<PlatoResponseDTO>> obtenerDisponibles() {
        List<Plato> platos = platoService.obtenerDisponibles();
        return ResponseEntity.ok(platoMapper.toResponseList(platos));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Obtener platos por categoría")
    public ResponseEntity<List<PlatoResponseDTO>> obtenerPorCategoria(@PathVariable String categoria) {
        List<Plato> platos = platoService.obtenerPorCategoria(categoria);
        return ResponseEntity.ok(platoMapper.toResponseList(platos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener plato por ID")
    public ResponseEntity<PlatoResponseDTO> obtenerPorId(@PathVariable Long id) {
        Plato plato = platoService.obtenerPorId(id);
        return ResponseEntity.ok(platoMapper.toResponse(plato));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo plato")
    public ResponseEntity<PlatoResponseDTO> crear(@RequestBody @Valid PlatoRequestDTO dto) {
        log.info("POST /api/v1/platos - nombre={}", dto.getNombre());
        Plato plato = platoMapper.toDomain(dto);
        Plato creado = platoService.crear(plato);
        return ResponseEntity.status(HttpStatus.CREATED).body(platoMapper.toResponse(creado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un plato existente")
    public ResponseEntity<PlatoResponseDTO> actualizar(@PathVariable Long id,
                                                        @RequestBody @Valid PlatoRequestDTO dto) {
        Plato nuevosDatos = platoMapper.toDomain(dto);
        Plato actualizado = platoService.actualizar(id, nuevosDatos);
        return ResponseEntity.ok(platoMapper.toResponse(actualizado));
    }

    @PatchMapping("/{id}/disponible")
    @Operation(summary = "Cambiar disponibilidad de un plato")
    public ResponseEntity<PlatoResponseDTO> cambiarDisponibilidad(@PathVariable Long id,
                                                                   @RequestParam boolean disponible) {
        Plato actualizado = platoService.cambiarDisponibilidad(id, disponible);
        return ResponseEntity.ok(platoMapper.toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un plato")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        platoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
