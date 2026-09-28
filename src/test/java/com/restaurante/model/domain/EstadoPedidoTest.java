package com.restaurante.model.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Documenta y protege las reglas de la maquina de estados del pedido, incluyendo la
 * decision de permitir cancelar un pedido en EN_PREPARACION (ver el Javadoc del enum).
 */
class EstadoPedidoTest {

    private static final List<EstadoPedido> ESTADOS = Arrays.asList(EstadoPedido.values());

    @Test
    @DisplayName("esCancelable es exactamente equivalente a poder pasar a CANCELADO")
    void esCancelable_equivaleAPoderCancelar() {
        for (EstadoPedido estado : ESTADOS) {
            assertEquals(estado.esCancelable(), estado.puedeTransicionarA(EstadoPedido.CANCELADO),
                    "Inconsistencia en el estado " + estado);
        }
    }

    @Test
    @DisplayName("un pedido se puede cancelar en RECIBIDO y en EN_PREPARACION")
    void pedidoCancelable_antesDeEstarListo() {
        assertTrue(EstadoPedido.RECIBIDO.esCancelable());
        assertTrue(EstadoPedido.EN_PREPARACION.esCancelable());
    }

    @Test
    @DisplayName("un pedido LISTO, ENTREGADO o CANCELADO ya no se cancela")
    void pedidoNoCancelable_cuandoYaSalioDeCocina() {
        assertFalse(EstadoPedido.LISTO.esCancelable());
        assertFalse(EstadoPedido.ENTREGADO.esCancelable());
        assertFalse(EstadoPedido.CANCELADO.esCancelable());
    }

    @Test
    @DisplayName("esFinal solo es true para ENTREGADO y CANCELADO")
    void esFinal_soloEstadosTerminales() {
        List<EstadoPedido> finales = ESTADOS.stream().filter(EstadoPedido::esFinal).toList();

        assertEquals(List.of(EstadoPedido.ENTREGADO, EstadoPedido.CANCELADO), finales);
    }

    @Test
    @DisplayName("un estado final no puede transicionar a ninguno")
    void estadoFinal_noTransiciona() {
        for (EstadoPedido estado : ESTADOS) {
            if (estado.esFinal()) {
                for (EstadoPedido siguiente : ESTADOS) {
                    assertFalse(estado.puedeTransicionarA(siguiente),
                            estado + " no deberia poder pasar a " + siguiente);
                }
            }
        }
    }

    @Test
    @DisplayName("el flujo principal RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO es valido")
    void flujoPrincipal_esValido() {
        assertTrue(EstadoPedido.RECIBIDO.puedeTransicionarA(EstadoPedido.EN_PREPARACION));
        assertTrue(EstadoPedido.EN_PREPARACION.puedeTransicionarA(EstadoPedido.LISTO));
        assertTrue(EstadoPedido.LISTO.puedeTransicionarA(EstadoPedido.ENTREGADO));
    }

    @Test
    @DisplayName("no se puede saltar ni retroceder de estado")
    void transicionesInvalidas_noPermitidas() {
        assertFalse(EstadoPedido.RECIBIDO.puedeTransicionarA(EstadoPedido.LISTO));
        assertFalse(EstadoPedido.RECIBIDO.puedeTransicionarA(EstadoPedido.RECIBIDO));
        assertFalse(EstadoPedido.LISTO.puedeTransicionarA(EstadoPedido.CANCELADO));
        assertFalse(EstadoPedido.LISTO.puedeTransicionarA(EstadoPedido.EN_PREPARACION));
    }
}
