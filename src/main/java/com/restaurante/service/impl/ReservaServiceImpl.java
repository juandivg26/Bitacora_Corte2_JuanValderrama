package com.restaurante.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.service.IMesaService;
import com.restaurante.service.IReservaService;
import com.restaurante.validator.IReservaValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservaServiceImpl implements IReservaService {

    private final Map<Long, Reserva> reservas = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    private final IMesaService mesaService;
    private final IReservaValidator validator;

    @Override
    public List<Reserva> obtenerTodas() {
        log.info("Obteniendo todas las reservas. Total: {}", reservas.size());
        return reservas.values().stream().toList();
    }

    @Override
    public List<Reserva> obtenerPorMesa(Long idMesa) {
        return reservas.values().stream()
                .filter(r -> r.getIdMesa().equals(idMesa))
                .toList();
    }

    @Override
    public Reserva obtenerPorId(Long id) {
        return reservas.values().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Reserva no encontrada: id={}", id);
                    return new RecursoNoEncontradoException("Reserva", id);
                });
    }

    @Override
    public Reserva crear(Reserva reserva) {
        Mesa mesa = mesaService.obtenerPorId(reserva.getIdMesa());
        validator.validarMesaDisponibleParaReserva(mesa);
        validator.validarFechaFutura(reserva.getFechaHora());

        reserva.setId(contador.getAndIncrement());
        reserva.setCancelada(false);
        reservas.put(reserva.getId(), reserva);

        mesaService.cambiarEstado(mesa.getId(), EstadoMesa.RESERVADA);
        log.info("Reserva creada: id={}, idMesa={}, cliente={}",
                reserva.getId(), reserva.getIdMesa(), reserva.getCliente());
        return reserva;
    }

    @Override
    public Reserva cancelar(Long id) {
        Reserva reserva = obtenerPorId(id);
        validator.validarPuedeModificarse(reserva);
        reserva.cancelar();

        Mesa mesa = mesaService.obtenerPorId(reserva.getIdMesa());
        if (mesa.getEstado() == EstadoMesa.RESERVADA) {
            mesaService.cambiarEstado(mesa.getId(), EstadoMesa.DISPONIBLE);
        }
        log.info("Reserva cancelada: id={}", id);
        return reserva;
    }

    @Override
    public Reserva reprogramar(Long id, LocalDateTime nuevaFechaHora) {
        Reserva reserva = obtenerPorId(id);
        validator.validarPuedeModificarse(reserva);
        validator.validarFechaFutura(nuevaFechaHora);
        reserva.reprogramar(nuevaFechaHora);
        log.info("Reserva reprogramada: id={}, nuevaFecha={}", id, nuevaFechaHora);
        return reserva;
    }
}
