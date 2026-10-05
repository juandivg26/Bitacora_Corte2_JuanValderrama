package com.restaurante.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.controller.docs.UsuarioApi;
import com.restaurante.mapper.UsuarioMapper;
import com.restaurante.model.domain.Usuario;
import com.restaurante.model.dto.response.UsuarioResponseDTO;
import com.restaurante.service.IUsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/usuarios")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMIN')")
public class UsuarioController implements UsuarioApi {

    private final IUsuarioService usuarioService;
    private final UsuarioMapper mapper;

    @Override
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos() {
        log.info("Consulta de lista de usuarios");
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(mapper.toResponseList(usuarios));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.info("Consulta de usuario id={}", id);
        Usuario usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }
}
