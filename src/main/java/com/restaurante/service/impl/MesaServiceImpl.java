package com.restaurante.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.service.IMesaService;
import com.restaurante.validator.IMesaValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MesaServiceImpl implements IMesaService {

    private final Map<Long, Mesa> mesas = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);
    private final IMesaValidator validator;

    @Override
    public List<Mesa> obtenerTodas() {
        log.info("Obteniendo todas las mesas. Total: {}", mesas.size());
        return mesas.values().stream().toList();
    }

    @Override
    public List<Mesa> obtenerDisponibles() {
        return mesas.values().stream()
                .filter(Mesa::estaDisponible)
                .toList();
    }

    @Override
    public Mesa obtenerPorId(Long id) {
        return mesas.values().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Mesa no encontrada: id={}", id);
                    return new RecursoNoEncontradoException("Mesa", id);
                });
    }

    @Override
    public Mesa crear(Mesa mesa) {
        validator.validarNumeroUnico(mesa.getNumero(), mesas.values());
        mesa.setId(contador.getAndIncrement());
        mesas.put(mesa.getId(), mesa);
        log.info("Mesa creada: id={}, numero={}", mesa.getId(), mesa.getNumero());
        return mesa;
    }

    @Override
    public Mesa cambiarEstado(Long id, EstadoMesa nuevoEstado) {
        Mesa mesa = obtenerPorId(id);
        validator.validarTransicionEstado(mesa, nuevoEstado);
        mesa.setEstado(nuevoEstado);
        log.info("Mesa id={} -> estado={}", id, nuevoEstado);
        return mesa;
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        mesas.remove(id);
        log.info("Mesa eliminada: id={}", id);
    }
}
