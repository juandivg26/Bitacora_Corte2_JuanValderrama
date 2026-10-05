package com.restaurante.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.entity.MesaEntity;
import com.restaurante.repository.IMesaRepository;
import com.restaurante.service.IMesaService;
import com.restaurante.validator.IMesaValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MesaServiceImpl implements IMesaService {

    private final IMesaRepository repository;
    private final MesaEntityMapper entityMapper;
    private final IMesaValidator validator;

    @Override
    public List<Mesa> obtenerTodas() {
        List<Mesa> mesas = repository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
        log.info("Obteniendo todas las mesas. Total: {}", mesas.size());
        return mesas;
    }

    @Override
    public List<Mesa> obtenerDisponibles() {
        return repository.findAll().stream()
                .map(entityMapper::toDomain)
                .filter(Mesa::estaDisponible)
                .toList();
    }

    @Override
    public Mesa obtenerPorId(Long id) {
        MesaEntity entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Mesa no encontrada: id={}", id);
                    return new RecursoNoEncontradoException("Mesa", id);
                });
        return entityMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Mesa crear(Mesa mesa) {
        validator.validarNumeroUnico(mesa.getNumero());
        
        // Inicializar valores por defecto
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesa.setCuentaAbierta(false);
        
        MesaEntity guardado = repository.save(entityMapper.toEntity(mesa));
        Mesa resultado = entityMapper.toDomain(guardado);
        log.info("Mesa creada: id={}, numero={}", resultado.getId(), resultado.getNumero());
        return resultado;
    }

    @Override
    @Transactional
    public Mesa cambiarEstado(Long id, EstadoMesa nuevoEstado) {
        Mesa mesa = obtenerPorId(id);
        validator.validarTransicionEstado(mesa, nuevoEstado);
        mesa.setEstado(nuevoEstado);
        MesaEntity actualizado = repository.save(entityMapper.toEntity(mesa));
        Mesa resultado = entityMapper.toDomain(actualizado);
        log.info("Mesa id={} -> estado={}", id, nuevoEstado);
        return resultado;
    }

    @Override
    @Transactional
    public Mesa abrirCuenta(Long idMesa) {
        Mesa mesa = obtenerPorId(idMesa);
        mesa.abrirCuenta();
        MesaEntity actualizado = repository.save(entityMapper.toEntity(mesa));
        Mesa resultado = entityMapper.toDomain(actualizado);
        log.info("Mesa id={} -> estado={}, cuentaAbierta={}",
                idMesa, resultado.getEstado(), resultado.getCuentaAbierta());
        return resultado;
    }

    @Override
    @Transactional
    public Mesa cerrarCuenta(Long idMesa) {
        Mesa mesa = obtenerPorId(idMesa);
        mesa.cerrarCuenta();
        MesaEntity actualizado = repository.save(entityMapper.toEntity(mesa));
        Mesa resultado = entityMapper.toDomain(actualizado);
        log.info("Mesa id={} -> estado={}, cuentaAbierta={}",
                idMesa, resultado.getEstado(), resultado.getCuentaAbierta());
        return resultado;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        obtenerPorId(id);
        repository.deleteById(id);
        log.info("Mesa eliminada: id={}", id);
    }
}
