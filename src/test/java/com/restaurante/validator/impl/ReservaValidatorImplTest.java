package com.restaurante.validator.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;

class ReservaValidatorImplTest {

    private final ReservaValidatorImpl validator = new ReservaValidatorImpl();

    @Test
    @DisplayName("validarMesaDisponibleParaReserva - mesa disponible no lanza excepción")
    void validarMesaDisponible_mesaDisponible_noLanza() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).estado(EstadoMesa.DISPONIBLE).build();

        assertDoesNotThrow(() -> validator.validarMesaDisponibleParaReserva(mesa));
    }

    @Test
    @DisplayName("validarMesaDisponibleParaReserva - mesa ocupada lanza ReglaDeNegocioException")
    void validarMesaDisponible_mesaOcupada_lanzaExcepcion() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).estado(EstadoMesa.OCUPADA).build();

        assertThrows(ReglaDeNegocioException.class, () -> validator.validarMesaDisponibleParaReserva(mesa));
    }

    @Test
    @DisplayName("validarFechaFutura - fecha futura no lanza excepción")
    void validarFechaFutura_fechaFutura_noLanza() {
        assertDoesNotThrow(() -> validator.validarFechaFutura(LocalDateTime.now().plusDays(1)));
    }

    @Test
    @DisplayName("validarFechaFutura - fecha pasada lanza ReglaDeNegocioException")
    void validarFechaFutura_fechaPasada_lanzaExcepcion() {
        LocalDateTime fechaPasada = LocalDateTime.now().minusDays(1);

        assertThrows(ReglaDeNegocioException.class, () -> validator.validarFechaFutura(fechaPasada));
    }

    @Test
    @DisplayName("validarPuedeModificarse - reserva cancelada lanza EstadoInvalidoException")
    void validarPuedeModificarse_cancelada_lanzaExcepcion() {
        Reserva reserva = Reserva.builder().id(1L).cancelada(true).build();

        assertThrows(EstadoInvalidoException.class, () -> validator.validarPuedeModificarse(reserva));
    }
}
