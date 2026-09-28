package com.restaurante.validator.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.util.FechaUtils;
import com.restaurante.validator.IReservaValidator;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReservaValidatorImpl implements IReservaValidator {

    /** Duracion de negocio que ocupa una reserva, usada para detectar solapamientos. */
    private static final Duration DURACION_RESERVA = Duration.ofHours(2);

    @Override
    public void validarMesaDisponibleParaReserva(Mesa mesa) {
        if (!mesa.estaDisponible()) {
            log.warn("Mesa no disponible para reserva: id={}, estado={}", mesa.getId(), mesa.getEstado());
            throw new ReglaDeNegocioException(
                    "La mesa " + mesa.getNumero() + " no está disponible para reservar (estado: "
                            + mesa.getEstado() + ")");
        }
    }

    @Override
    public void validarFechaFutura(LocalDateTime fechaHora) {
        if (!FechaUtils.esFechaFutura(fechaHora)) {
            log.warn("Fecha de reserva no es futura: {}", FechaUtils.formatearIso(fechaHora));
            throw new ReglaDeNegocioException("La reserva debe ser a una fecha y hora futuras");
        }
    }

    @Override
    public void validarSinSolapamiento(Long idMesa, LocalDateTime fechaHora, List<Reserva> reservasDeLaMesa) {
        if (fechaHora == null || reservasDeLaMesa == null || reservasDeLaMesa.isEmpty()) {
            return;
        }

        LocalDateTime inicioNueva = fechaHora;
        LocalDateTime finNueva = fechaHora.plus(DURACION_RESERVA);

        boolean seSolapa = reservasDeLaMesa.stream()
                .filter(Reserva::estaVigente)
                .filter(reserva -> reserva.getFechaHora() != null)
                .anyMatch(reserva -> FechaUtils.seSolapan(inicioNueva, finNueva,
                        reserva.getFechaHora(), reserva.getFechaHora().plus(DURACION_RESERVA)));

        if (seSolapa) {
            log.warn("Reserva solapada para mesa id={} en {}", idMesa, FechaUtils.formatearIso(fechaHora));
            throw new ReglaDeNegocioException(
                    "La mesa " + idMesa + " ya tiene una reserva vigente que se solapa con "
                            + FechaUtils.formatearFecha(fechaHora) + " (bloque de "
                            + DURACION_RESERVA.toHours() + " horas)");
        }
    }

    @Override
    public void validarPuedeModificarse(Reserva reserva) {
        if (!reserva.estaVigente()) {
            log.warn("Reserva no modificable, ya está cancelada: id={}", reserva.getId());
            throw new EstadoInvalidoException("La reserva id=" + reserva.getId() + " ya fue cancelada");
        }
    }
}
