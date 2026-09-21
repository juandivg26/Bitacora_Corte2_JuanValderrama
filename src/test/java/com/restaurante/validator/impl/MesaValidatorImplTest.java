package com.restaurante.validator.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;

class MesaValidatorImplTest {

    private final MesaValidatorImpl validator = new MesaValidatorImpl();

    @Test
    @DisplayName("validarNumeroUnico - número nuevo no lanza excepción")
    void validarNumeroUnico_numeroNuevo_noLanza() {
        List<Mesa> existentes = List.of(Mesa.builder().numero(1).build());

        assertDoesNotThrow(() -> validator.validarNumeroUnico(2, existentes));
    }

    @Test
    @DisplayName("validarNumeroUnico - número duplicado lanza ConflictoException")
    void validarNumeroUnico_numeroDuplicado_lanzaConflicto() {
        List<Mesa> existentes = List.of(Mesa.builder().numero(1).build());

        assertThrows(ConflictoException.class, () -> validator.validarNumeroUnico(1, existentes));
    }

    @Test
    @DisplayName("validarTransicionEstado - transición válida no lanza excepción")
    void validarTransicionEstado_valida_noLanza() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).estado(EstadoMesa.DISPONIBLE).build();

        assertDoesNotThrow(() -> validator.validarTransicionEstado(mesa, EstadoMesa.OCUPADA));
    }

    @Test
    @DisplayName("validarTransicionEstado - transición inválida lanza EstadoInvalidoException")
    void validarTransicionEstado_invalida_lanzaExcepcion() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).estado(EstadoMesa.OCUPADA).build();

        assertThrows(EstadoInvalidoException.class,
                () -> validator.validarTransicionEstado(mesa, EstadoMesa.RESERVADA));
    }
}
