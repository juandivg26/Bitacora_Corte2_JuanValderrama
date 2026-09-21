package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.service.IMesaService;
import com.restaurante.service.IPlatoService;
import com.restaurante.validator.IPedidoValidator;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private IPlatoService platoService;

    @Mock
    private IMesaService mesaService;

    @Mock
    private IPedidoValidator validator;

    @InjectMocks
    private PedidoServiceImpl service;

    private Mesa mesaDisponible;
    private Plato platoDisponible;

    @BeforeEach
    void setUp() {
        mesaDisponible = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build();
        platoDisponible = Plato.builder().id(1L).nombre("Bandeja Paisa")
                .precio(28000.0).categoria("PRINCIPALES").disponible(true).build();
    }

    private Pedido pedidoConUnItem() {
        List<ItemPedido> items = new ArrayList<>();
        items.add(ItemPedido.builder().idPlato(1L).cantidad(2).build());
        return Pedido.builder().idMesa(1L).items(items).estado(EstadoPedido.RECIBIDO).build();
    }

    @Test
    @DisplayName("crear - congela el precio del plato y marca la mesa como OCUPADA")
    void crear_pedidoCorrecto_congelaPrecioYOcupaMesa() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);

        Pedido resultado = service.crear(pedidoConUnItem());

        assertNotNull(resultado.getId());
        assertEquals(28000.0, resultado.getItems().get(0).getPrecioCongelado());
        assertEquals("Bandeja Paisa", resultado.getItems().get(0).getNombrePlato());
        verify(mesaService, times(1)).cambiarEstado(1L, EstadoMesa.OCUPADA);
    }

    @Test
    @DisplayName("crear - mesa no disponible lanza ReglaDeNegocioException")
    void crear_mesaNoDisponible_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        doThrow(new ReglaDeNegocioException("Mesa no disponible"))
                .when(validator).validarMesaDisponible(any());

        assertThrows(ReglaDeNegocioException.class, () -> service.crear(pedidoConUnItem()));
    }

    @Test
    @DisplayName("crear - plato no disponible lanza ReglaDeNegocioException")
    void crear_platoNoDisponible_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);
        doThrow(new ReglaDeNegocioException("Plato no disponible"))
                .when(validator).validarPlatoDisponible(any());

        assertThrows(ReglaDeNegocioException.class, () -> service.crear(pedidoConUnItem()));
    }

    @Test
    @DisplayName("agregarItem - pedido no modificable lanza EstadoInvalidoException")
    void agregarItem_pedidoNoModificable_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);
        Pedido creado = service.crear(pedidoConUnItem());

        doThrow(new EstadoInvalidoException("No modificable"))
                .when(validator).validarPuedeModificarse(any());

        ItemPedido nuevoItem = ItemPedido.builder().idPlato(1L).cantidad(1).build();
        assertThrows(EstadoInvalidoException.class,
                () -> service.agregarItem(creado.getId(), nuevoItem));
    }

    @Test
    @DisplayName("agregarItem - pedido modificable agrega el ítem y congela su precio")
    void agregarItem_pedidoModificable_agregaItem() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);
        Pedido creado = service.crear(pedidoConUnItem());

        ItemPedido nuevoItem = ItemPedido.builder().idPlato(1L).cantidad(1).build();
        Pedido resultado = service.agregarItem(creado.getId(), nuevoItem);

        assertEquals(2, resultado.getItems().size());
    }

    @Test
    @DisplayName("cambiarEstado - transición válida actualiza el estado")
    void cambiarEstado_transicionValida_actualizaEstado() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);
        Pedido creado = service.crear(pedidoConUnItem());

        Pedido resultado = service.cambiarEstado(creado.getId(), EstadoPedido.EN_PREPARACION);

        assertEquals(EstadoPedido.EN_PREPARACION, resultado.getEstado());
    }

    @Test
    @DisplayName("cambiarEstado - transición inválida lanza EstadoInvalidoException")
    void cambiarEstado_transicionInvalida_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);
        Pedido creado = service.crear(pedidoConUnItem());

        doThrow(new EstadoInvalidoException("Transición inválida"))
                .when(validator).validarTransicionEstado(any(), eq(EstadoPedido.ENTREGADO));

        assertThrows(EstadoInvalidoException.class,
                () -> service.cambiarEstado(creado.getId(), EstadoPedido.ENTREGADO));
    }

    @Test
    @DisplayName("obtenerPorMesa - filtra solo los pedidos de esa mesa")
    void obtenerPorMesa_filtraCorrectamente() {
        when(mesaService.obtenerPorId(anyLong())).thenReturn(mesaDisponible);
        lenient().when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);
        service.crear(pedidoConUnItem());

        List<Pedido> resultado = service.obtenerPorMesa(1L);

        assertEquals(1, resultado.size());
    }
}
