package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.mapper.CuentaEntityMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.entity.CuentaEntity;
import com.restaurante.repository.ICuentaRepository;
import com.restaurante.service.IMesaService;
import com.restaurante.service.IPedidoService;
import com.restaurante.validator.ICuentaValidator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CuentaServiceImplTest {

    @Mock
    private ICuentaRepository repository;

    @Mock
    private CuentaEntityMapper entityMapper;

    @Mock
    private IMesaService mesaService;

    @Mock
    private IPedidoService pedidoService;

    @Mock
    private ICuentaValidator validator;

    @InjectMocks
    private CuentaServiceImpl service;

    private Mesa mesaOcupadaSinCuenta;
    private UUID cuentaId;

    @BeforeEach
    void setUp() {
        cuentaId = UUID.randomUUID();
        
        // Configurar mapper dominio -> entidad
        lenient().when(entityMapper.toEntity(any(Cuenta.class))).thenAnswer(inv -> {
            Cuenta cuenta = inv.getArgument(0);
            CuentaEntity entity = CuentaEntity.builder()
                    .id(cuenta.getId())
                    .idMesa(cuenta.getIdMesa())
                    .total(cuenta.getTotal())
                    .estado(cuenta.getEstado())
                    .fechaApertura(cuenta.getFechaApertura())
                    .build();
            // Si el ID es null, asignar uno temporal para el test
            if (entity.getId() == null) {
                entity.setId(1L);
            }
            return entity;
        });

        // Configurar mapper entidad -> dominio
        lenient().when(entityMapper.toDomain(any(CuentaEntity.class))).thenAnswer(inv -> {
            CuentaEntity entity = inv.getArgument(0);
            return Cuenta.builder()
                    .id(entity.getId())
                    .idMesa(entity.getIdMesa())
                    .total(entity.getTotal())
                    .estado(entity.getEstado())
                    .fechaApertura(entity.getFechaApertura())
                    .build();
        });

        // Configurar repository
        lenient().when(repository.save(any(CuentaEntity.class))).thenAnswer(inv -> {
            CuentaEntity entity = inv.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(1L);
            }
            return entity;
        });
        lenient().when(repository.findById(anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            if (id.equals(1L)) {
                return Optional.of(CuentaEntity.builder().id(1L).idMesa(1L).total(0.0)
                        .estado(EstadoCuenta.ABIERTA).fechaApertura(LocalDateTime.now()).build());
            }
            return Optional.empty();
        });
        lenient().when(repository.findAll()).thenReturn(new ArrayList<>());
        lenient().when(repository.findByIdMesa(anyLong())).thenReturn(new ArrayList<>());

        mesaOcupadaSinCuenta = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.OCUPADA).cuentaAbierta(false).build();
    }

    @Test
    @DisplayName("abrir - mesa sin cuenta abierta crea la cuenta")
    void abrir_mesaSinCuenta_creaCuenta() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaOcupadaSinCuenta);
        lenient().when(repository.findByIdMesa(1L)).thenReturn(new ArrayList<>());

        Cuenta resultado = service.abrir(1L);

        assertNotNull(resultado.getId());
        assertEquals(EstadoCuenta.ABIERTA, resultado.getEstado());
        assertEquals(1L, resultado.getIdMesa());
        assertEquals(0.0, resultado.getTotal());
        // La mesa debe marcarse con cuenta abierta (y persistirse)
        verify(mesaService, times(1)).abrirCuenta(1L);
    }

    @Test
    @DisplayName("abrir - mesa con cuenta ya abierta lanza ReglaDeNegocioException")
    void abrir_mesaConCuentaAbierta_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaOcupadaSinCuenta);
        List<CuentaEntity> cuentas = List.of(
            CuentaEntity.builder().id(1L).idMesa(1L).estado(EstadoCuenta.ABIERTA).build()
        );
        when(repository.findByIdMesa(1L)).thenReturn(cuentas);
        doThrow(new ReglaDeNegocioException("Ya tiene cuenta abierta"))
                .when(validator).validarMesaSinCuentaAbierta(any());

        assertThrows(ReglaDeNegocioException.class, () -> service.abrir(1L));
    }

    @Test
    @DisplayName("registrarPago - calcula el total a partir de los ítems de los pedidos de la mesa")
    void registrarPago_calculaTotalDesdePedidos() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaOcupadaSinCuenta);
        lenient().when(repository.findByIdMesa(1L)).thenReturn(new ArrayList<>());
        
        // Crear cuenta manualmente porque el abrir usa el mapper
        when(repository.findById(1L)).thenReturn(Optional.of(
            CuentaEntity.builder().id(1L).idMesa(1L).total(0.0)
                    .estado(EstadoCuenta.ABIERTA).fechaApertura(LocalDateTime.now()).build()
        ));
        when(repository.save(any(CuentaEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        
        Cuenta resultado = service.registrarPago(1L);

        assertEquals(0.0, resultado.getTotal());
        assertEquals(EstadoCuenta.EN_PAGO, resultado.getEstado());
    }

    @Test
    @DisplayName("registrarPago - ignora los pedidos CANCELADOS al calcular el total")
    void registrarPago_ignoraPedidosCancelados() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaOcupadaSinCuenta);
        lenient().when(repository.findByIdMesa(1L)).thenReturn(new ArrayList<>());

        when(repository.findById(1L)).thenReturn(Optional.of(
            CuentaEntity.builder().id(1L).idMesa(1L).total(0.0)
                    .estado(EstadoCuenta.ABIERTA).fechaApertura(LocalDateTime.now()).build()
        ));
        when(repository.save(any(CuentaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido pedidoActivo = Pedido.builder()
                .idMesa(1L)
                .estado(EstadoPedido.RECIBIDO)
                .items(List.of(ItemPedido.builder().idPlato(1L).nombrePlato("Hamburguesa")
                        .precioCongelado(20000.0).cantidad(2).build()))
                .build();
        Pedido pedidoCancelado = Pedido.builder()
                .idMesa(1L)
                .estado(EstadoPedido.CANCELADO)
                .items(List.of(ItemPedido.builder().idPlato(2L).nombrePlato("Pizza")
                        .precioCongelado(50000.0).cantidad(1).build()))
                .build();
        when(pedidoService.obtenerPorMesa(1L)).thenReturn(List.of(pedidoActivo, pedidoCancelado));

        Cuenta resultado = service.registrarPago(1L);

        // Solo cuenta el pedido activo: 20000.0 * 2 = 40000.0; el cancelado se ignora
        assertEquals(40000.0, resultado.getTotal());
        assertEquals(EstadoCuenta.EN_PAGO, resultado.getEstado());
    }

    @Test
    @DisplayName("obtenerPorId - ID inexistente lanza RecursoNoEncontradoException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    @DisplayName("cerrar - cierra la cuenta y libera la mesa")
    void cerrar_cuentaEnPago_liberaMesa() {
        when(repository.findById(1L)).thenReturn(Optional.of(
            CuentaEntity.builder().id(1L).idMesa(1L).total(0.0)
                    .estado(EstadoCuenta.EN_PAGO).fechaApertura(LocalDateTime.now()).build()
        ));
        when(repository.save(any(CuentaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuenta resultado = service.cerrar(1L);

        assertEquals(EstadoCuenta.CERRADA, resultado.getEstado());
        // La mesa debe liberarse (y persistirse) al cerrar la cuenta
        verify(mesaService, times(1)).cerrarCuenta(1L);
    }

    @Test
    @DisplayName("obtenerPorMesa - no encuentra cuenta abierta si ya está cerrada")
    void obtenerPorMesa_cuentaCerrada_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaOcupadaSinCuenta);
        List<CuentaEntity> cuentas = List.of(
            CuentaEntity.builder().id(1L).idMesa(1L).total(0.0)
                    .estado(EstadoCuenta.CERRADA).fechaApertura(LocalDateTime.now()).build()
        );
        when(repository.findByIdMesa(1L)).thenReturn(cuentas);

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorMesa(1L));
    }
}