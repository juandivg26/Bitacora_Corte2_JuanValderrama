package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.model.domain.Plato;
import com.restaurante.validator.IPlatoValidator;

@ExtendWith(MockitoExtension.class)
class PlatoServiceImplTest {

    @Mock
    private IPlatoValidator validator;

    @InjectMocks
    private PlatoServiceImpl service;

    @Test
    @DisplayName("crear - guarda el plato y le asigna un ID")
    void crear_platoCorrecto_guardaYRetorna() {
        Plato entrada = Plato.builder()
                .nombre("Bandeja Paisa").precio(28000.0)
                .categoria("PRINCIPALES").disponible(true).build();

        Plato resultado = service.crear(entrada);

        assertNotNull(resultado.getId());
        assertEquals("Bandeja Paisa", resultado.getNombre());
        verify(validator, times(1)).validarNombreUnico(eq("Bandeja Paisa"), any());
    }

    @Test
    @DisplayName("crear - nombre duplicado lanza ConflictoException")
    void crear_nombreDuplicado_lanzaConflicto() {
        doThrow(new ConflictoException("Nombre duplicado"))
                .when(validator).validarNombreUnico(any(), any());

        Plato plato = Plato.builder().nombre("Bandeja Paisa")
                .precio(28000.0).categoria("PRINCIPALES").build();

        assertThrows(ConflictoException.class, () -> service.crear(plato));
    }

    @Test
    @DisplayName("obtenerPorId - ID inexistente lanza RecursoNoEncontradoException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    @DisplayName("obtenerTodos - sin platos devuelve lista vacía")
    void obtenerTodos_sinPlatos_devuelveListaVacia() {
        List<Plato> resultado = service.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("obtenerDisponibles - filtra solo los disponibles")
    void obtenerDisponibles_filtraSoloDisponibles() {
        service.crear(Plato.builder().nombre("A").precio(10.0)
                .categoria("X").disponible(true).build());
        service.crear(Plato.builder().nombre("B").precio(10.0)
                .categoria("X").disponible(false).build());

        List<Plato> resultado = service.obtenerDisponibles();

        assertEquals(1, resultado.size());
        assertEquals("A", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("obtenerPorCategoria - filtra ignorando mayúsculas/minúsculas")
    void obtenerPorCategoria_filtraCorrectamente() {
        service.crear(Plato.builder().nombre("Ajiaco").precio(20000.0)
                .categoria("SOPAS").disponible(true).build());
        service.crear(Plato.builder().nombre("Bandeja").precio(28000.0)
                .categoria("PRINCIPALES").disponible(true).build());

        List<Plato> resultado = service.obtenerPorCategoria("sopas");

        assertEquals(1, resultado.size());
        assertEquals("Ajiaco", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("cambiarDisponibilidad - desactiva un plato existente")
    void cambiarDisponibilidad_desactiva_correctamente() {
        Plato creado = service.crear(Plato.builder().nombre("Sopa")
                .precio(15000.0).categoria("ENTRADAS").disponible(true).build());

        Plato resultado = service.cambiarDisponibilidad(creado.getId(), false);

        assertFalse(resultado.estaDisponible());
    }

    @Test
    @DisplayName("eliminar - borra el plato existente")
    void eliminar_platoExistente_loElimina() {
        Plato creado = service.crear(Plato.builder().nombre("Sopa")
                .precio(15000.0).categoria("ENTRADAS").disponible(true).build());

        service.eliminar(creado.getId());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(creado.getId()));
    }
}
