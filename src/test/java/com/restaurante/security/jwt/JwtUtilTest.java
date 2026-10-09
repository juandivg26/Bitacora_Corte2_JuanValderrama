package com.restaurante.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "TXkyc3VwZXJzZWNyZXRrZXlwYXJhZWxyZXN0YXVyYW50ZTIwMjY=");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000L);
    }

    @Test
    @DisplayName("Generar token JWT y extraer claims correctamente")
    void generarToken_tokenValido_extraeClaims() {
        String email = "admin@americanbites.com";
        String rol = "ADMINISTRADOR";

        String token = jwtUtil.generateToken(email, rol);

        assertNotNull(token);
        assertTrue(jwtUtil.isValid(token));
        assertEquals(email, jwtUtil.extractEmail(token));
        assertEquals(rol, jwtUtil.extractRol(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("Validar token inválido o corrupto retorna false")
    void isValid_tokenInvalido_retornaFalse() {
        assertFalse(jwtUtil.isValid("token.completamente.invalido"));
        assertFalse(jwtUtil.isValid(null));
        assertFalse(jwtUtil.isValid(""));
    }

    @Test
    @DisplayName("Validar token expirado retorna false")
    void isValid_tokenExpirado_retornaFalse() {
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -1000L); // Token ya expirado
        String token = jwtUtil.generateToken("expirado@americanbites.com", "CLIENTE");

        assertFalse(jwtUtil.isValid(token));
        assertTrue(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("Secreto de menos de 32 bytes se rechaza en lugar de rellenarse")
    void generarToken_secretoCorto_lanzaExcepcion() {
        ReflectionTestUtils.setField(jwtUtil, "secret", "corto");

        assertThrows(IllegalStateException.class, () -> jwtUtil.generateToken("a@b.com", "CLIENTE"));
    }
}
