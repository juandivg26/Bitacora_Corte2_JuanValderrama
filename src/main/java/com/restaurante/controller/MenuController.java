package com.restaurante.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.restaurante.mapper.PlatoMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.IPlatoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Menú", description = "Vista de solo lectura de la carta para el cliente (solo platos disponibles)")
public class MenuController {

    private final IPlatoService platoService;
    private final PlatoMapper platoMapper;

    @GetMapping
    @Operation(summary = "Consultar el menú (solo platos disponibles)")
    public ResponseEntity<List<PlatoResponseDTO>> verMenu() {
        log.info("GET /api/v1/menu");
        List<Plato> disponibles = platoService.obtenerDisponibles();
        return ResponseEntity.ok(platoMapper.toResponseList(disponibles));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Consultar el menú filtrado por categoría (solo disponibles)")
    public ResponseEntity<List<PlatoResponseDTO>> verMenuPorCategoria(@PathVariable String categoria) {
        List<Plato> disponibles = platoService.obtenerPorCategoria(categoria).stream()
                .filter(Plato::estaDisponible)
                .toList();
        return ResponseEntity.ok(platoMapper.toResponseList(disponibles));
    }
}
