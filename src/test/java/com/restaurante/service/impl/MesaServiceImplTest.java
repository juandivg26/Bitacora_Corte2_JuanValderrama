package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.validator.IMesaValidator;

@ExtendWith(MockitoExtension.class)
class MesaServiceImplTest {

    @Mock
    private IMesaValidator validator;

    @InjectMocks
    private MesaServiceImpl service;

    private Mesa mesaBase() {
        return Mesa.builder().numero(1).capacidad(4)
                .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build();
    }

    @Test
    @DisplayName("crear - guarda la mesa y le asigna un ID")
    void crear_mesaCorrecta_guardaYRetorna() {
        Mesa resultado = service.crear(mesaBase());

        assertNotNull(resultado.getId());
        assertEquals(1, resultado.getNumero());
        verify(validator, times(1)).validarNumeroUnico(eq(1), any());
    }

    @Test
    @DisplayName("crear - número duplicado lanza ConflictoException")
    void crear_numeroDuplicado_lanzaConflicto() {
        doThrow(new ConflictoException("Número duplicado"))
                .when(validator).validarNumeroUnico(any(), any());

        assertThrows(ConflictoException.class, () -> service.crear(mesaBase()));
    }

    @Test
    @DisplayName("obtenerPorId - ID inexistente lanza RecursoNoEncontradoException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    @DisplayName("obtenerDisponibles - filtra solo las disponibles")
    void obtenerDisponibles_filtraSoloDisponibles() {
        service.crear(mesaBase());
        service.crear(Mesa.builder().numero(2).capacidad(2)
                .estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build());

        List<Mesa> resultado = service.obtenerDisponibles();

        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getNumero());
    }

    @Test
    @DisplayName("cambiarEstado - transición inválida lanza EstadoInvalidoException")
    void cambiarEstado_transicionInvalida_lanzaExcepcion() {
        Mesa creada = service.crear(mesaBase());
        doThrow(new EstadoInvalidoException("Transición inválida"))
                .when(validator).validarTransicionEstado(any(), any());

        assertThrows(EstadoInvalidoException.class,
                () -> service.cambiarEstado(creada.getId(), EstadoMesa.RESERVADA));
    }

    @Test
    @DisplayName("cambiarEstado - a OCUPADA actualiza el estado sin tocar cuentaAbierta")
    void cambiarEstado_aOcupada_actualizaSoloElEstado() {
        Mesa creada = service.crear(mesaBase());

        Mesa resultado = service.cambiarEstado(creada.getId(), EstadoMesa.OCUPADA);

        assertEquals(EstadoMesa.OCUPADA, resultado.getEstado());
        assertFalse(resultado.getCuentaAbierta());
    }

    @Test
    @DisplayName("eliminar - borra la mesa existente")
    void eliminar_mesaExistente_laElimina() {
        Mesa creada = service.crear(mesaBase());

        service.eliminar(creada.getId());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(creada.getId()));
    }
}
