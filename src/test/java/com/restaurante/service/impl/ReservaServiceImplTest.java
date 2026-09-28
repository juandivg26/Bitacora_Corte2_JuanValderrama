package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.mapper.ReservaEntityMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;
import com.restaurante.model.entity.ReservaEntity;
import com.restaurante.repository.IReservaRepository;
import com.restaurante.service.IMesaService;
import com.restaurante.util.UuidV7Generator;
import com.restaurante.validator.IReservaValidator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReservaServiceImplTest {

    @Mock
    private IReservaRepository repository;

    @Mock
    private ReservaEntityMapper entityMapper;

    @Mock
    private IMesaService mesaService;

    @Mock
    private IReservaValidator validator;

    @InjectMocks
    private ReservaServiceImpl service;

    private Mesa mesaDisponible;
    private UUID reservaId;

    @BeforeEach
    void setUp() {
        reservaId = UuidV7Generator.generate();
        
        // Configurar mapper dominio -> entidad
        lenient().when(entityMapper.toEntity(any(Reserva.class))).thenAnswer(inv -> {
            Reserva reserva = inv.getArgument(0);
            return ReservaEntity.builder()
                    .id(reserva.getId())
                    .idMesa(reserva.getIdMesa())
                    .cliente(reserva.getCliente())
                    .fechaHora(reserva.getFechaHora())
                    .comensales(reserva.getComensales())
                    .cancelada(reserva.getCancelada())
                    .build();
        });

        // Configurar mapper entidad -> dominio
        lenient().when(entityMapper.toDomain(any(ReservaEntity.class))).thenAnswer(inv -> {
            ReservaEntity entity = inv.getArgument(0);
            return Reserva.builder()
                    .id(entity.getId())
                    .idMesa(entity.getIdMesa())
                    .cliente(entity.getCliente())
                    .fechaHora(entity.getFechaHora())
                    .comensales(entity.getComensales())
                    .cancelada(entity.getCancelada() != null && entity.getCancelada())
                    .build();
        });

        // Configurar repository
        lenient().when(repository.save(any(ReservaEntity.class))).thenAnswer(inv -> {
            ReservaEntity entity = inv.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(reservaId);
            }
            return entity;
        });
        lenient().when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());
        lenient().when(repository.findAll()).thenReturn(new ArrayList<>());
        lenient().when(repository.findByIdMesa(anyLong())).thenReturn(new ArrayList<>());

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
        when(repository.save(any(ReservaEntity.class))).thenAnswer(inv -> {
            ReservaEntity entity = inv.getArgument(0);
            entity.setId(reservaId);
            return entity;
        });

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
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(UUID.randomUUID()));
    }

    @Test
    @DisplayName("cancelar - reserva vigente se cancela y libera la mesa")
    void cancelar_reservaVigente_liberaMesa() {
        ReservaEntity entity = ReservaEntity.builder().id(reservaId).idMesa(1L).cliente("Juan Pérez")
                .fechaHora(LocalDateTime.now().plusDays(1)).comensales(4).cancelada(false).build();
        when(repository.findById(reservaId)).thenReturn(Optional.of(entity));
        when(repository.save(any(ReservaEntity.class))).thenAnswer(inv -> {
            ReservaEntity res = inv.getArgument(0);
            res.setCancelada(true);
            return res;
        });
        
        Mesa mesaReservada = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.RESERVADA).cuentaAbierta(false).build();
        lenient().when(mesaService.obtenerPorId(1L)).thenReturn(mesaReservada);
        lenient().when(mesaService.obtenerPorId(anyLong())).thenReturn(mesaReservada);

        Reserva resultado = service.cancelar(reservaId);

        assertEquals(Boolean.TRUE, resultado.getCancelada());
        verify(mesaService, times(1)).cambiarEstado(1L, EstadoMesa.DISPONIBLE);
    }

    @Test
    @DisplayName("cancelar - reserva ya cancelada lanza EstadoInvalidoException")
    void cancelar_yaCancelada_lanzaExcepcion() {
        ReservaEntity entity = ReservaEntity.builder().id(reservaId).idMesa(1L).cliente("Juan Pérez")
                .fechaHora(LocalDateTime.now().plusDays(1)).comensales(4).cancelada(true).build();
        when(repository.findById(reservaId)).thenReturn(Optional.of(entity));
        doThrow(new EstadoInvalidoException("Ya cancelada"))
                .when(validator).validarPuedeModificarse(any());

        assertThrows(EstadoInvalidoException.class, () -> service.cancelar(reservaId));
    }

    @Test
    @DisplayName("reprogramar - actualiza la fecha de la reserva")
    void reprogramar_reservaVigente_actualizaFecha() {
        ReservaEntity entity = ReservaEntity.builder().id(reservaId).idMesa(1L).cliente("Juan Pérez")
                .fechaHora(LocalDateTime.now().plusDays(1)).comensales(4).cancelada(false).build();
        when(repository.findById(reservaId)).thenReturn(Optional.of(entity));
        when(repository.save(any(ReservaEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);

        LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(3);
        Reserva resultado = service.reprogramar(reservaId, nuevaFecha);

        assertEquals(nuevaFecha, resultado.getFechaHora());
    }

    @Test
    @DisplayName("obtenerPorMesa - filtra solo las reservas de esa mesa")
    void obtenerPorMesa_filtraCorrectamente() {
        ReservaEntity entity = ReservaEntity.builder().id(reservaId).idMesa(1L).cliente("Juan Pérez")
                .fechaHora(LocalDateTime.now().plusDays(1)).comensales(4).cancelada(false).build();
        when(repository.findByIdMesa(1L)).thenReturn(List.of(entity));

        List<Reserva> resultado = service.obtenerPorMesa(1L);

        assertEquals(1, resultado.size());
    }
}