package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.UsuarioEntityMapper;
import com.restaurante.model.domain.Rol;
import com.restaurante.model.domain.Usuario;
import com.restaurante.model.entity.UsuarioEntity;
import com.restaurante.repository.IUsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private IUsuarioRepository repository;

    @Mock
    private UsuarioEntityMapper entityMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private UsuarioEntity entity;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .email("admin@americanbites.com")
                .password("Admin123*")
                .nombre("Admin")
                .rol(Rol.ADMINISTRADOR)
                .fechaCreacion(LocalDateTime.now())
                .build();

        entity = UsuarioEntity.builder()
                .id(1L)
                .email("admin@americanbites.com")
                .password("$2a$10$encoded")
                .nombre("Admin")
                .rol(Rol.ADMINISTRADOR)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Obtener todos los usuarios")
    void obtenerTodos_retornaLista() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(usuario);

        List<Usuario> resultado = usuarioService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("admin@americanbites.com", resultado.get(0).getEmail());
    }

    @Test
    @DisplayName("Obtener usuario por ID existente")
    void obtenerPorId_existente_retornaUsuario() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(usuario);

        Usuario resultado = usuarioService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Obtener usuario por ID inexistente lanza RecursoNoEncontradoException")
    void obtenerPorId_inexistente_lanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Obtener usuario por email existente")
    void obtenerPorEmail_existente_retornaUsuario() {
        when(repository.findByEmail("admin@americanbites.com")).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(usuario);

        Usuario resultado = usuarioService.obtenerPorEmail("admin@americanbites.com");

        assertNotNull(resultado);
        assertEquals("admin@americanbites.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Obtener usuario por email inexistente lanza RecursoNoEncontradoException")
    void obtenerPorEmail_inexistente_lanzaExcepcion() {
        when(repository.findByEmail("noexiste@americanbites.com")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.obtenerPorEmail("noexiste@americanbites.com"));
    }

    @Test
    @DisplayName("Registrar nuevo usuario hashea la contraseña y asigna rol por defecto si es nulo")
    void registrar_usuarioNuevo_hasheaPassword() {
        Usuario nuevo = Usuario.builder()
                .email("nuevo@americanbites.com")
                .password("Clave123")
                .nombre("Nuevo Usuario")
                .build();

        when(repository.existsByEmail("nuevo@americanbites.com")).thenReturn(false);
        when(passwordEncoder.encode("Clave123")).thenReturn("$2a$10$hashed");
        when(entityMapper.toEntity(any(Usuario.class))).thenReturn(entity);
        when(repository.save(any(UsuarioEntity.class))).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(usuario);

        Usuario resultado = usuarioService.registrar(nuevo);

        assertNotNull(resultado);
        verify(passwordEncoder).encode("Clave123");
        verify(repository).save(any(UsuarioEntity.class));
    }

    @Test
    @DisplayName("Registrar usuario con email duplicado lanza ConflictoException")
    void registrar_emailDuplicado_lanzaConflictoException() {
        when(repository.existsByEmail("admin@americanbites.com")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> usuarioService.registrar(usuario));
    }
}
