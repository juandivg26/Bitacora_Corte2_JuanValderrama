package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.MesaEntityMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.entity.MesaEntity;
import com.restaurante.repository.IMesaRepository;
import com.restaurante.validator.IMesaValidator;

@ExtendWith(MockitoExtension.class)
class MesaServiceImplTest {

    @Mock
    private IMesaRepository repository;

    @Mock
    private MesaEntityMapper entityMapper;

    @Mock
    private IMesaValidator validator;

    @InjectMocks
    private MesaServiceImpl service;

    @BeforeEach
    void setUp() {
        // Configurar mapper dominio -> entidad
        lenient().when(entityMapper.toEntity(any(Mesa.class))).thenAnswer(inv -> {
            Mesa mesa = inv.getArgument(0);
            return MesaEntity.builder()
                    .id(mesa.getId())
                    .numero(mesa.getNumero())
                    .capacidad(mesa.getCapacidad())
                    .estado(mesa.getEstado())
                    .cuentaAbierta(mesa.getCuentaAbierta())
                    .build();
        });

        // Configurar mapper entidad -> dominio
        lenient().when(entityMapper.toDomain(any(MesaEntity.class))).thenAnswer(inv -> {
            MesaEntity entity = inv.getArgument(0);
            return Mesa.builder()
                    .id(entity.getId())
                    .numero(entity.getNumero())
                    .capacidad(entity.getCapacidad())
                    .estado(entity.getEstado())
                    .cuentaAbierta(entity.getCuentaAbierta())
                    .build();
        });

        // Configurar repository para save
        lenient().when(repository.save(any(MesaEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        
        // Configurar repository para findById
        lenient().when(repository.findById(anyLong())).thenReturn(Optional.empty());
        
        // Configurar repository para findAll
        lenient().when(repository.findAll()).thenReturn(new ArrayList<>());
    }

    private Mesa mesaBase() {
        return Mesa.builder().numero(1).capacidad(4)
                .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build();
    }

    @Test
    @DisplayName("crear - guarda la mesa y le asigna un ID")
    void crear_mesaCorrecta_guardaYRetorna() {
        when(repository.save(any(MesaEntity.class))).thenAnswer(inv -> {
            MesaEntity entity = inv.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        Mesa resultado = service.crear(mesaBase());

        assertNotNull(resultado.getId());
        assertEquals(1, resultado.getNumero());
        assertEquals(EstadoMesa.DISPONIBLE, resultado.getEstado());
        assertEquals(false, resultado.getCuentaAbierta());
        verify(validator, times(1)).validarNumeroUnico(1);
    }

    @Test
    @DisplayName("crear - número duplicado lanza ConflictoException")
    void crear_numeroDuplicado_lanzaConflicto() {
        doThrow(new ConflictoException("Número duplicado"))
                .when(validator).validarNumeroUnico(1);

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
        List<MesaEntity> mesas = List.of(
            MesaEntity.builder().id(1L).numero(1).capacidad(4)
                    .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build(),
            MesaEntity.builder().id(2L).numero(2).capacidad(2)
                    .estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build()
        );
        when(repository.findAll()).thenReturn(mesas);

        List<Mesa> resultado = service.obtenerDisponibles();

        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getNumero());
    }

    @Test
    @DisplayName("cambiarEstado - transición inválida lanza EstadoInvalidoException")
    void cambiarEstado_transicionInvalida_lanzaExcepcion() {
        Mesa mesa = mesaBase();
        mesa.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(
            MesaEntity.builder().id(1L).numero(1).capacidad(4)
                    .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build()
        ));
        doThrow(new EstadoInvalidoException("Transición inválida"))
                .when(validator).validarTransicionEstado(any(), any());

        assertThrows(EstadoInvalidoException.class,
                () -> service.cambiarEstado(1L, EstadoMesa.RESERVADA));
    }

    @Test
    @DisplayName("cambiarEstado - a OCUPADA actualiza el estado sin tocar cuentaAbierta")
    void cambiarEstado_aOcupada_actualizaSoloElEstado() {
        when(repository.findById(1L)).thenReturn(Optional.of(
            MesaEntity.builder().id(1L).numero(1).capacidad(4)
                    .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build()
        ));
        when(repository.save(any(MesaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Mesa resultado = service.cambiarEstado(1L, EstadoMesa.OCUPADA);

        assertEquals(EstadoMesa.OCUPADA, resultado.getEstado());
        assertFalse(resultado.getCuentaAbierta());
    }

    @Test
    @DisplayName("abrirCuenta - persiste estado OCUPADA y cuentaAbierta=true")
    void abrirCuenta_mesaDisponible_persisteCambios() {
        when(repository.findById(1L)).thenReturn(Optional.of(
            MesaEntity.builder().id(1L).numero(1).capacidad(4)
                    .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build()
        ));
        when(repository.save(any(MesaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Mesa resultado = service.abrirCuenta(1L);

        assertEquals(EstadoMesa.OCUPADA, resultado.getEstado());
        assertEquals(true, resultado.getCuentaAbierta());
        verify(repository, times(1)).save(any(MesaEntity.class));
    }

    @Test
    @DisplayName("cerrarCuenta - persiste estado DISPONIBLE y cuentaAbierta=false")
    void cerrarCuenta_mesaOcupada_persisteCambios() {
        when(repository.findById(1L)).thenReturn(Optional.of(
            MesaEntity.builder().id(1L).numero(1).capacidad(4)
                    .estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build()
        ));
        when(repository.save(any(MesaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Mesa resultado = service.cerrarCuenta(1L);

        assertEquals(EstadoMesa.DISPONIBLE, resultado.getEstado());
        assertEquals(false, resultado.getCuentaAbierta());
        verify(repository, times(1)).save(any(MesaEntity.class));
    }

    @Test
    @DisplayName("eliminar - borra la mesa existente")
    void eliminar_mesaExistente_laElimina() {
        when(repository.findById(1L)).thenReturn(Optional.of(
            MesaEntity.builder().id(1L).numero(1).capacidad(4)
                    .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build()
        ));

        service.eliminar(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}