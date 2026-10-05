package com.restaurante.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.controller.docs.MenuApi;
import com.restaurante.mapper.PlatoMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.response.PlatoResponseDTO;
import com.restaurante.service.IPlatoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Slf4j
public class MenuController implements MenuApi {

    private final IPlatoService platoService;
    private final PlatoMapper platoMapper;

    @Override
    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> verMenu() {
        log.info("GET /api/v1/menu");
        List<Plato> disponibles = platoService.obtenerDisponibles();
        return ResponseEntity.ok(platoMapper.toResponseList(disponibles));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> verPlatoDelMenu(@PathVariable Long id) {
        log.info("GET /api/v1/menu/{}", id);
        Plato plato = platoService.obtenerDisponiblePorId(id);
        return ResponseEntity.ok(platoMapper.toResponse(plato));
    }

    @Override
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<PlatoResponseDTO>> verMenuPorCategoria(@PathVariable String categoria) {
        log.info("GET /api/v1/menu/categoria/{}", categoria);
        List<Plato> disponibles = platoService.obtenerPorCategoria(categoria).stream()
                .filter(Plato::estaDisponible)
                .toList();
        return ResponseEntity.ok(platoMapper.toResponseList(disponibles));
    }
}
