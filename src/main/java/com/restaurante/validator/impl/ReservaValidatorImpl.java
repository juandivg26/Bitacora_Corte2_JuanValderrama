package com.restaurante.validator.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.validator.IReservaValidator;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReservaValidatorImpl implements IReservaValidator {

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
        if (fechaHora == null || !fechaHora.isAfter(LocalDateTime.now())) {
            log.warn("Fecha de reserva no es futura: {}", fechaHora);
            throw new ReglaDeNegocioException("La reserva debe ser a una fecha y hora futuras");
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
