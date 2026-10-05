package com.restaurante.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.restaurante.model.domain.Rol;
import com.restaurante.model.entity.UsuarioEntity;
import com.restaurante.repository.IUsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private IUsuarioRepository repository;

    @InjectMocks
    private UsuarioDetailsService service;

    @Test
    @DisplayName("Cargar usuario existente por email devuelve UserDetails con sus roles")
    void loadUserByUsername_usuarioExistente_retornaUserDetails() {
        UsuarioEntity entity = UsuarioEntity.builder()
                .id(1L)
                .email("admin@americanbites.com")
                .password("$2a$10$hashedPassword")
                .rol(Rol.ADMINISTRADOR)
                .build();

        when(repository.findByEmail("admin@americanbites.com")).thenReturn(Optional.of(entity));

        UserDetails details = service.loadUserByUsername("admin@americanbites.com");

        assertNotNull(details);
        assertEquals("admin@americanbites.com", details.getUsername());
        assertEquals("$2a$10$hashedPassword", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR")));
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Cargar usuario inexistente lanza UsernameNotFoundException")
    void loadUserByUsername_usuarioInexistente_lanzaExcepcion() {
        when(repository.findByEmail("noexiste@americanbites.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("noexiste@americanbites.com"));
    }
}
