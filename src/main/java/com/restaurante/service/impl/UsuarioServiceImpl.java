package com.restaurante.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.UsuarioEntityMapper;
import com.restaurante.model.domain.Rol;
import com.restaurante.model.domain.Usuario;
import com.restaurante.model.entity.UsuarioEntity;
import com.restaurante.repository.IUsuarioRepository;
import com.restaurante.service.IUsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioRepository repository;
    private final UsuarioEntityMapper entityMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> obtenerTodos() {
        log.info("Obteniendo todos los usuarios");
        return repository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Usuario obtenerPorId(Long id) {
        UsuarioEntity entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado por id: {}", id);
                    return new RecursoNoEncontradoException("Usuario", id);
                });
        return entityMapper.toDomain(entity);
    }

    @Override
    public Usuario obtenerPorEmail(String email) {
        UsuarioEntity entity = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado por email: {}", email);
                    return new RecursoNoEncontradoException("Usuario con email", email);
                });
        return entityMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Usuario registrar(Usuario usuario) {
        if (repository.existsByEmail(usuario.getEmail())) {
            log.warn("Intento de registro con email ya existente: {}", usuario.getEmail());
            throw new ConflictoException("Ya existe un usuario registrado con el email: " + usuario.getEmail());
        }

        // SEGURIDAD: Siempre se asigna rol CLIENTE, se ignora cualquier rol enviado en el request
        // para evitar que un usuario malicioso se registre como ADMINISTRADOR
        usuario.setRol(Rol.CLIENTE);

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setFechaCreacion(LocalDateTime.now());

        UsuarioEntity guardado = repository.save(entityMapper.toEntity(usuario));
        log.info("Usuario registrado exitosamente: id={}, email={}, rol={}",
                guardado.getId(), guardado.getEmail(), guardado.getRol());

        return entityMapper.toDomain(guardado);
    }
}
