package com.restaurante.validator;

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
import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.repository.IMesaRepository;
import com.restaurante.validator.impl.MesaValidatorImpl;

@ExtendWith(MockitoExtension.class)
class MesaValidatorImplTest {

    @Mock
    private IMesaRepository mesaRepository;

    @InjectMocks
    private MesaValidatorImpl validator;

    @Test
    @DisplayName("validarNumeroUnico - número nuevo no lanza excepción")
    void validarNumeroUnico_numeroNuevo_noLanza() {
        when(mesaRepository.existsByNumero(2)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validarNumeroUnico(2));
    }

    @Test
    @DisplayName("validarNumeroUnico - número duplicado lanza ConflictoException")
    void validarNumeroUnico_numeroDuplicado_lanzaConflicto() {
        when(mesaRepository.existsByNumero(1)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> validator.validarNumeroUnico(1));
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
