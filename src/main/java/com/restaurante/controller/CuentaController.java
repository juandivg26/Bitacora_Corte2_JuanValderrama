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

import com.restaurante.mapper.CuentaMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.dto.request.CuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.service.ICuentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Cuentas", description = "Gestión del cobro y cierre de cuentas por mesa")
public class CuentaController {

    private final ICuentaService cuentaService;
    private final CuentaMapper cuentaMapper;

    @GetMapping
    @Operation(summary = "Obtener todas las cuentas")
    public ResponseEntity<List<CuentaResponseDTO>> obtenerTodas() {
        log.info("GET /api/v1/cuentas");
        List<Cuenta> cuentas = cuentaService.obtenerTodas();
        return ResponseEntity.ok(cuentaMapper.toResponseList(cuentas));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID")
    public ResponseEntity<CuentaResponseDTO> obtenerPorId(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.obtenerPorId(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }

    @GetMapping("/mesa/{idMesa}")
    @Operation(summary = "Obtener la cuenta abierta de una mesa")
    public ResponseEntity<CuentaResponseDTO> obtenerPorMesa(@PathVariable Long idMesa) {
        Cuenta cuenta = cuentaService.obtenerPorMesa(idMesa);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }

    @PostMapping
    @Operation(summary = "Abrir la cuenta de una mesa")
    public ResponseEntity<CuentaResponseDTO> abrir(@RequestBody @Valid CuentaRequestDTO dto) {
        log.info("POST /api/v1/cuentas - idMesa={}", dto.getIdMesa());
        Cuenta cuenta = cuentaService.abrir(dto.getIdMesa());
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaMapper.toResponse(cuenta));
    }

    @PatchMapping("/{id}/pago")
    @Operation(summary = "Registrar el pago de una cuenta")
    public ResponseEntity<CuentaResponseDTO> registrarPago(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.registrarPago(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }

    @PatchMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar una cuenta")
    public ResponseEntity<CuentaResponseDTO> cerrar(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.cerrar(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }
}
