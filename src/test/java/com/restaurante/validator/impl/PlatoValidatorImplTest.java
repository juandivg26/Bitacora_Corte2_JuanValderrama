package com.restaurante.validator.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.exception.ConflictoException;
import com.restaurante.repository.IPlatoRepository;

@ExtendWith(MockitoExtension.class)
class PlatoValidatorImplTest {

    @Mock
    private IPlatoRepository platoRepository;

    @InjectMocks
    private PlatoValidatorImpl validator;

    @Test
    @DisplayName("validarNombreUnico - nombre nuevo no lanza excepción")
    void validarNombreUnico_nombreNuevo_noLanza() {
        when(platoRepository.existsByNombreIgnoreCase("Bandeja Paisa")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validarNombreUnico("Bandeja Paisa"));
    }

    @Test
    @DisplayName("validarNombreUnico - nombre duplicado lanza ConflictoException")
    void validarNombreUnico_nombreDuplicado_lanzaConflicto() {
        when(platoRepository.existsByNombreIgnoreCase("AJIACO")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> validator.validarNombreUnico("AJIACO"));
    }
}
