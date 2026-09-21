package com.restaurante.validator.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;

class PedidoValidatorImplTest {

    private final PedidoValidatorImpl validator = new PedidoValidatorImpl();

    @Test
    @DisplayName("validarMesaDisponible - mesa disponible no lanza excepción")
    void validarMesaDisponible_mesaDisponible_noLanza() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).estado(EstadoMesa.DISPONIBLE).build();

        assertDoesNotThrow(() -> validator.validarMesaDisponible(mesa));
    }

    @Test
    @DisplayName("validarMesaDisponible - mesa ocupada lanza ReglaDeNegocioException")
    void validarMesaDisponible_mesaOcupada_lanzaExcepcion() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).estado(EstadoMesa.OCUPADA).build();

        assertThrows(ReglaDeNegocioException.class, () -> validator.validarMesaDisponible(mesa));
    }

    @Test
    @DisplayName("validarPlatoDisponible - plato agotado lanza ReglaDeNegocioException")
    void validarPlatoDisponible_platoAgotado_lanzaExcepcion() {
        Plato plato = Plato.builder().id(1L).nombre("Ajiaco").disponible(false).build();

        assertThrows(ReglaDeNegocioException.class, () -> validator.validarPlatoDisponible(plato));
    }

    @Test
    @DisplayName("validarPuedeModificarse - pedido en preparación lanza EstadoInvalidoException")
    void validarPuedeModificarse_enPreparacion_lanzaExcepcion() {
        Pedido pedido = Pedido.builder().id(1L).estado(EstadoPedido.EN_PREPARACION).build();

        assertThrows(EstadoInvalidoException.class, () -> validator.validarPuedeModificarse(pedido));
    }

    @Test
    @DisplayName("validarTransicionEstado - de RECIBIDO a LISTO lanza EstadoInvalidoException")
    void validarTransicionEstado_saltoInvalido_lanzaExcepcion() {
        Pedido pedido = Pedido.builder().id(1L).estado(EstadoPedido.RECIBIDO).build();

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarTransicionEstado(pedido, EstadoPedido.LISTO));
    }
}
