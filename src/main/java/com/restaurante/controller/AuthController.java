package com.restaurante.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurante.controller.docs.AuthApi;
import com.restaurante.mapper.UsuarioMapper;
import com.restaurante.model.domain.Usuario;
import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.RegistroUsuarioRequestDTO;
import com.restaurante.model.dto.response.TokenResponseDTO;
import com.restaurante.model.dto.response.UsuarioResponseDTO;
import com.restaurante.security.jwt.JwtUtil;
import com.restaurante.service.IUsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final IUsuarioService usuarioService;
    private final UsuarioMapper mapper;

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        log.info("Intento de login para email: {}", dto.getEmail());
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        UserDetails user = (UserDetails) auth.getPrincipal();
        Usuario usuario = usuarioService.obtenerPorEmail(user.getUsername());

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());
        log.info("Login exitoso para email={}, rol={}", usuario.getEmail(), usuario.getRol());

        TokenResponseDTO response = TokenResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .expiraEnMs(jwtUtil.getExpirationMs())
                .build();

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistroUsuarioRequestDTO dto) {
        log.info("Solicitud de registro de usuario: email={}", dto.getEmail());
        Usuario nuevo = mapper.toDomain(dto);
        Usuario guardado = usuarioService.registrar(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(guardado));
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioService.obtenerPorEmail(authentication.getName());
        return ResponseEntity.ok(mapper.toResponse(usuario));
    }
}
