package com.restaurante.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.entity.PlatoEntity;
import com.restaurante.repository.IPlatoRepository;
import com.restaurante.service.IPlatoService;
import com.restaurante.validator.IPlatoValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlatoServiceImpl implements IPlatoService {

    private final IPlatoRepository repository;
    private final PlatoEntityMapper entityMapper;
    private final IPlatoValidator validator;

    @Override
    public List<Plato> obtenerTodos() {
        List<Plato> platos = repository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
        log.info("Obteniendo todos los platos. Total: {}", platos.size());
        return platos;
    }

    @Override
    public List<Plato> obtenerDisponibles() {
        return repository.findAll().stream()
                .map(entityMapper::toDomain)
                .filter(Plato::estaDisponible)
                .toList();
    }

    @Override
    public List<Plato> obtenerPorCategoria(String categoria) {
        return repository.findAll().stream()
                .map(entityMapper::toDomain)
                .filter(plato -> plato.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }

    @Override
    public Plato obtenerPorId(Long id) {
        PlatoEntity entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Plato no encontrado: id={}", id);
                    return new RecursoNoEncontradoException("Plato", id);
                });
        return entityMapper.toDomain(entity);
    }

    @Override
    public Plato crear(Plato plato) {
        validator.validarNombreUnico(plato.getNombre());
        plato.setDisponible(true);
        PlatoEntity guardado = repository.save(entityMapper.toEntity(plato));
        Plato resultado = entityMapper.toDomain(guardado);
        log.info("Plato creado: id={}, nombre={}", resultado.getId(), resultado.getNombre());
        return resultado;
    }

    @Override
    public Plato actualizar(Long id, Plato nuevosDatos) {
        Plato existente = obtenerPorId(id);
        boolean nombreCambiado = !existente.getNombre().equalsIgnoreCase(nuevosDatos.getNombre());
        if (nombreCambiado) {
            validator.validarNombreUnico(nuevosDatos.getNombre());
        }

        existente.setNombre(nuevosDatos.getNombre());
        existente.setPrecio(nuevosDatos.getPrecio());
        existente.setCategoria(nuevosDatos.getCategoria());

        PlatoEntity actualizado = repository.save(entityMapper.toEntity(existente));
        Plato resultado = entityMapper.toDomain(actualizado);
        log.info("Plato actualizado: id={}", id);
        return resultado;
    }

    @Override
    public Plato cambiarDisponibilidad(Long id, boolean disponible) {
        Plato plato = obtenerPorId(id);
        if (disponible) {
            plato.activar();
        } else {
            plato.desactivar();
        }

        PlatoEntity actualizado = repository.save(entityMapper.toEntity(plato));
        Plato resultado = entityMapper.toDomain(actualizado);
        log.info("Plato id={} -> disponible={}", id, disponible);
        return resultado;
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        repository.deleteById(id);
        log.info("Plato eliminado: id={}", id);
    }
}
