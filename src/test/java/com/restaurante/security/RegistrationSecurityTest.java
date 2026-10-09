package com.restaurante.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.domain.Rol;
import com.restaurante.model.dto.request.RegistroUsuarioRequestDTO;
import com.restaurante.model.entity.UsuarioEntity;
import com.restaurante.repository.IUsuarioRepository;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:securitydb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=VGVzdFNlY3JldFBhcmFQcnVlYmFzSldUQW1lcmljYW5CaXRlczIwMjY=",
        "logging.level.org.mongodb.driver.cluster=OFF"
})
@AutoConfigureMockMvc
class RegistrationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Registro: aunque envíe rol ADMINISTRADOR, debe crearse como CLIENTE")
    void registrar_enviaAdmin_forzaCliente() throws Exception {
        RegistroUsuarioRequestDTO request = RegistroUsuarioRequestDTO.builder()
                .email("hacker@americanbites.com")
                .password("PasswordSeguro123")
                .nombre("Hacker Malicioso")
                .rol(Rol.ADMINISTRADOR) // Intenta registrarse como admin
                .build();

        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("hacker@americanbites.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE")); // ← Debe ser CLIENTE, no ADMIN

        // Verificar en BD que realmente fue guardado como CLIENTE
        Optional<UsuarioEntity> guardado = usuarioRepository.findByEmail("hacker@americanbites.com");
        assertTrue(guardado.isPresent());
        assertEquals(Rol.CLIENTE, guardado.get().getRol());
    }

    @Test
    @DisplayName("Registro: aunque envíe rol MESERO, debe crearse como CLIENTE")
    void registrar_enviaMesero_forzaCliente() throws Exception {
        RegistroUsuarioRequestDTO request = RegistroUsuarioRequestDTO.builder()
                .email("falso-mesero@americanbites.com")
                .password("PasswordSeguro123")
                .nombre("Falso Mesero")
                .rol(Rol.MESERO)
                .build();

        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }
}