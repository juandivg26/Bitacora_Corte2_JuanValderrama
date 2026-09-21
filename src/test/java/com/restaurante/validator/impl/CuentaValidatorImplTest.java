package com.restaurante.validator.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.Mesa;

class CuentaValidatorImplTest {

    private final CuentaValidatorImpl validator = new CuentaValidatorImpl();

    @Test
    @DisplayName("validarMesaSinCuentaAbierta - mesa sin cuenta no lanza excepción")
    void validarMesaSinCuentaAbierta_sinCuenta_noLanza() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).cuentaAbierta(false).build();

        assertDoesNotThrow(() -> validator.validarMesaSinCuentaAbierta(mesa));
    }

    @Test
    @DisplayName("validarMesaSinCuentaAbierta - mesa con cuenta abierta lanza ReglaDeNegocioException")
    void validarMesaSinCuentaAbierta_conCuenta_lanzaExcepcion() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).cuentaAbierta(true).build();

        assertThrows(ReglaDeNegocioException.class, () -> validator.validarMesaSinCuentaAbierta(mesa));
    }

    @Test
    @DisplayName("validarTransicionEstado - de CERRADA a cualquier estado lanza EstadoInvalidoException")
    void validarTransicionEstado_desdeCerrada_lanzaExcepcion() {
        Cuenta cuenta = Cuenta.builder().id(1L).estado(EstadoCuenta.CERRADA).build();

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarTransicionEstado(cuenta, EstadoCuenta.ABIERTA));
    }
}
