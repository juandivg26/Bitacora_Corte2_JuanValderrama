package com.restaurante.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.ReservaEntityMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.entity.ReservaEntity;
import com.restaurante.repository.IReservaRepository;
import com.restaurante.service.IMesaService;
import com.restaurante.service.IReservaService;
import com.restaurante.util.UuidV7Generator;
import com.restaurante.validator.IReservaValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservaServiceImpl implements IReservaService {

    private final IReservaRepository repository;
    private final ReservaEntityMapper entityMapper;
    private final IMesaService mesaService;
    private final IReservaValidator validator;

    @Override
    public List<Reserva> obtenerTodas() {
        List<Reserva> reservas = repository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
        log.info("Obteniendo todas las reservas. Total: {}", reservas.size());
        return reservas;
    }

    @Override
    public List<Reserva> obtenerPorMesa(Long idMesa) {
        return repository.findByIdMesa(idMesa).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Reserva obtenerPorId(UUID id) {
        ReservaEntity entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Reserva no encontrada: id={}", id);
                    return new RecursoNoEncontradoException("Reserva", id);
                });
        return entityMapper.toDomain(entity);
    }

    @Override
    public Reserva crear(Reserva reserva) {
        Mesa mesa = mesaService.obtenerPorId(reserva.getIdMesa());
        validator.validarMesaDisponibleParaReserva(mesa);
        validator.validarFechaFutura(reserva.getFechaHora());

        reserva.setId(UuidV7Generator.generate());
        reserva.setCancelada(false);
        
        ReservaEntity guardado = repository.save(entityMapper.toEntity(reserva));
        Reserva resultado = entityMapper.toDomain(guardado);

        mesaService.cambiarEstado(mesa.getId(), EstadoMesa.RESERVADA);
        log.info("Reserva creada: id={}, idMesa={}, cliente={}",
                resultado.getId(), resultado.getIdMesa(), resultado.getCliente());
        return resultado;
    }

    @Override
    public Reserva cancelar(UUID id) {
        Reserva reserva = obtenerPorId(id);
        validator.validarPuedeModificarse(reserva);
        reserva.cancelar();

        ReservaEntity actualizado = repository.save(entityMapper.toEntity(reserva));
        Reserva resultado = entityMapper.toDomain(actualizado);

        Mesa mesa = mesaService.obtenerPorId(reserva.getIdMesa());
        if (mesa.getEstado() == EstadoMesa.RESERVADA) {
            mesaService.cambiarEstado(mesa.getId(), EstadoMesa.DISPONIBLE);
        }
        log.info("Reserva cancelada: id={}", id);
        return resultado;
    }

    @Override
    public Reserva reprogramar(UUID id, LocalDateTime nuevaFechaHora) {
        Reserva reserva = obtenerPorId(id);
        validator.validarPuedeModificarse(reserva);
        validator.validarFechaFutura(nuevaFechaHora);
        reserva.reprogramar(nuevaFechaHora);
        
        ReservaEntity actualizado = repository.save(entityMapper.toEntity(reserva));
        Reserva resultado = entityMapper.toDomain(actualizado);
        
        log.info("Reserva reprogramada: id={}, nuevaFecha={}", id, nuevaFechaHora);
        return resultado;
    }
}
