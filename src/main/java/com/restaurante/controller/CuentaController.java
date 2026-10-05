package com.restaurante.controller;

import java.util.List;

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

import com.restaurante.controller.docs.CuentaApi;
import com.restaurante.mapper.CuentaMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.dto.request.CuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;
import com.restaurante.service.ICuentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
@Slf4j
public class CuentaController implements CuentaApi {

    private final ICuentaService cuentaService;
    private final CuentaMapper cuentaMapper;

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO', 'CAJERO')")
    public ResponseEntity<List<CuentaResponseDTO>> obtenerTodas() {
        log.info("GET /api/v1/cuentas");
        List<Cuenta> cuentas = cuentaService.obtenerTodas();
        return ResponseEntity.ok(cuentaMapper.toResponseList(cuentas));
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO', 'CAJERO')")
    public ResponseEntity<CuentaResponseDTO> obtenerPorId(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.obtenerPorId(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }

    @Override
    @GetMapping("/mesa/{idMesa}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO', 'CAJERO')")
    public ResponseEntity<CuentaResponseDTO> obtenerPorMesa(@PathVariable Long idMesa) {
        Cuenta cuenta = cuentaService.obtenerPorMesa(idMesa);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'MESERO')")
    public ResponseEntity<CuentaResponseDTO> abrir(@RequestBody @Valid CuentaRequestDTO dto) {
        log.info("POST /api/v1/cuentas - idMesa={}", dto.getIdMesa());
        Cuenta cuenta = cuentaService.abrir(dto.getIdMesa());
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaMapper.toResponse(cuenta));
    }

    @Override
    @PatchMapping("/{id}/pago")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'CAJERO')")
    public ResponseEntity<CuentaResponseDTO> registrarPago(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.registrarPago(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }

    @Override
    @PatchMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN', 'CAJERO')")
    public ResponseEntity<CuentaResponseDTO> cerrar(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.cerrar(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }
}
