package com.restaurante.security.oauth2;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.domain.Rol;
import com.restaurante.model.dto.response.TokenResponseDTO;
import com.restaurante.model.entity.UsuarioEntity;
import com.restaurante.repository.IUsuarioRepository;
import com.restaurante.security.jwt.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final IUsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        if (email == null) {
            log.warn("Login OAuth2 fallido: Google no proporcionó email");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email no proporcionado por proveedor OAuth2");
            return;
        }

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("Creando nuevo usuario a partir de login OAuth2: {}", email);
                    UsuarioEntity nuevo = UsuarioEntity.builder()
                            .email(email)
                            .nombre(name != null ? name : email)
                            .password("{oauth2}google")
                            .rol(Rol.CLIENTE)
                            .fechaCreacion(LocalDateTime.now())
                            .build();
                    return usuarioRepository.save(nuevo);
                });

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().name());
        log.info("Login OAuth2 exitoso para email={}, rol={}", email, usuario.getRol());

        TokenResponseDTO tokenResponse = TokenResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .expiraEnMs(jwtUtil.getExpirationMs())
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
    }
}
