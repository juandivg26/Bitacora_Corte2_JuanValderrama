package com.restaurante.validator.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.exception.ConflictoException;
import com.restaurante.model.domain.Plato;

class PlatoValidatorImplTest {

    private final PlatoValidatorImpl validator = new PlatoValidatorImpl();

    @Test
    @DisplayName("validarNombreUnico - nombre nuevo no lanza excepción")
    void validarNombreUnico_nombreNuevo_noLanza() {
        List<Plato> existentes = List.of(Plato.builder().nombre("Ajiaco").build());

        assertDoesNotThrow(() -> validator.validarNombreUnico("Bandeja Paisa", existentes));
    }

    @Test
    @DisplayName("validarNombreUnico - nombre duplicado (case-insensitive) lanza ConflictoException")
    void validarNombreUnico_nombreDuplicado_lanzaConflicto() {
        List<Plato> existentes = List.of(Plato.builder().nombre("Ajiaco").build());

        assertThrows(ConflictoException.class, () -> validator.validarNombreUnico("AJIACO", existentes));
    }

    @Test
    @DisplayName("validarNombreUnico - lista vacía no lanza excepción")
    void validarNombreUnico_listaVacia_noLanza() {
        assertDoesNotThrow(() -> validator.validarNombreUnico("Ajiaco", List.of()));
    }
}
