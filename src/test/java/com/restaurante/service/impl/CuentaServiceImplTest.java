package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.service.IMesaService;
import com.restaurante.service.IPedidoService;
import com.restaurante.validator.ICuentaValidator;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    private IMesaService mesaService;

    @Mock
    private IPedidoService pedidoService;

    @Mock
    private ICuentaValidator validator;

    @InjectMocks
    private CuentaServiceImpl service;

    private Mesa mesaSinCuenta;

    @BeforeEach
    void setUp() {
        mesaSinCuenta = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.OCUPADA).cuentaAbierta(false).build();
    }

    @Test
    @DisplayName("abrir - mesa sin cuenta abierta crea la cuenta")
    void abrir_mesaSinCuenta_creaCuenta() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaSinCuenta);

        Cuenta resultado = service.abrir(1L);

        assertNotNull(resultado.getId());
        assertEquals(EstadoCuenta.ABIERTA, resultado.getEstado());
        assertEquals(EstadoMesa.OCUPADA, mesaSinCuenta.getEstado());
        assertEquals(Boolean.TRUE, mesaSinCuenta.getCuentaAbierta());
    }

    @Test
    @DisplayName("abrir - mesa con cuenta ya abierta lanza ReglaDeNegocioException")
    void abrir_mesaConCuentaAbierta_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaSinCuenta);
        doThrow(new ReglaDeNegocioException("Ya tiene cuenta abierta"))
                .when(validator).validarMesaSinCuentaAbierta(any());

        assertThrows(ReglaDeNegocioException.class, () -> service.abrir(1L));
    }

    @Test
    @DisplayName("registrarPago - calcula el total a partir de los ítems de los pedidos de la mesa")
    void registrarPago_calculaTotalDesdePedidos() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaSinCuenta);
        Cuenta cuenta = service.abrir(1L);

        ItemPedido item = ItemPedido.builder().idPlato(1L).nombrePlato("Bandeja Paisa")
                .precioCongelado(28000.0).cantidad(2).build();
        Pedido pedido = Pedido.builder().idMesa(1L).items(List.of(item)).build();
        when(pedidoService.obtenerPorMesa(1L)).thenReturn(List.of(pedido));

        Cuenta resultado = service.registrarPago(cuenta.getId());

        assertEquals(56000.0, resultado.getTotal());
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
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaSinCuenta);
        Cuenta cuenta = service.abrir(1L);
        when(pedidoService.obtenerPorMesa(1L)).thenReturn(List.of());
        service.registrarPago(cuenta.getId());

        Cuenta resultado = service.cerrar(cuenta.getId());

        assertEquals(EstadoCuenta.CERRADA, resultado.getEstado());
        assertEquals(EstadoMesa.DISPONIBLE, mesaSinCuenta.getEstado());
        assertEquals(Boolean.FALSE, mesaSinCuenta.getCuentaAbierta());
    }

    @Test
    @DisplayName("obtenerPorMesa - no encuentra cuenta abierta si ya está cerrada")
    void obtenerPorMesa_cuentaCerrada_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaSinCuenta);
        Cuenta cuenta = service.abrir(1L);
        when(pedidoService.obtenerPorMesa(1L)).thenReturn(List.of());
        service.registrarPago(cuenta.getId());
        service.cerrar(cuenta.getId());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorMesa(1L));
    }
}
