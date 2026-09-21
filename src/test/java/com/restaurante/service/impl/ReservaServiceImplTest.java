package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.service.IMesaService;
import com.restaurante.validator.IReservaValidator;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private IMesaService mesaService;

    @Mock
    private IReservaValidator validator;

    @InjectMocks
    private ReservaServiceImpl service;

    private Mesa mesaDisponible;

    @BeforeEach
    void setUp() {
        mesaDisponible = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build();
    }

    private Reserva reservaBase() {
        return Reserva.builder().idMesa(1L).cliente("Juan Pérez")
                .fechaHora(LocalDateTime.now().plusDays(1)).comensales(4).build();
    }

    @Test
    @DisplayName("crear - guarda la reserva y marca la mesa como RESERVADA")
    void crear_reservaCorrecta_guardaYReservaMesa() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);

        Reserva resultado = service.crear(reservaBase());

        assertNotNull(resultado.getId());
        assertFalse(resultado.getCancelada());
        verify(mesaService, times(1)).cambiarEstado(1L, EstadoMesa.RESERVADA);
    }

    @Test
    @DisplayName("crear - mesa no disponible lanza ReglaDeNegocioException")
    void crear_mesaNoDisponible_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        doThrow(new ReglaDeNegocioException("Mesa no disponible"))
                .when(validator).validarMesaDisponibleParaReserva(any());

        assertThrows(ReglaDeNegocioException.class, () -> service.crear(reservaBase()));
    }

    @Test
    @DisplayName("obtenerPorId - ID inexistente lanza RecursoNoEncontradoException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        assertThrows(com.restaurante.exception.RecursoNoEncontradoException.class,
                () -> service.obtenerPorId(99L));
    }

    @Test
    @DisplayName("cancelar - reserva vigente se cancela y libera la mesa si sigue RESERVADA")
    void cancelar_reservaVigente_liberaMesa() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        Reserva creada = service.crear(reservaBase());
        mesaDisponible.setEstado(EstadoMesa.RESERVADA);

        Reserva resultado = service.cancelar(creada.getId());

        assertEquals(Boolean.TRUE, resultado.getCancelada());
        verify(mesaService, times(1)).cambiarEstado(1L, EstadoMesa.DISPONIBLE);
    }

    @Test
    @DisplayName("cancelar - reserva ya cancelada lanza EstadoInvalidoException")
    void cancelar_yaCancelada_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        Reserva creada = service.crear(reservaBase());
        doThrow(new EstadoInvalidoException("Ya cancelada"))
                .when(validator).validarPuedeModificarse(any());

        assertThrows(EstadoInvalidoException.class, () -> service.cancelar(creada.getId()));
    }

    @Test
    @DisplayName("reprogramar - actualiza la fecha de la reserva")
    void reprogramar_reservaVigente_actualizaFecha() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        Reserva creada = service.crear(reservaBase());
        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(3);

        Reserva resultado = service.reprogramar(creada.getId(), nuevaFecha);

        assertEquals(nuevaFecha, resultado.getFechaHora());
    }

    @Test
    @DisplayName("obtenerPorMesa - filtra solo las reservas de esa mesa")
    void obtenerPorMesa_filtraCorrectamente() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        service.crear(reservaBase());

        assertEquals(1, service.obtenerPorMesa(1L).size());
    }
}
