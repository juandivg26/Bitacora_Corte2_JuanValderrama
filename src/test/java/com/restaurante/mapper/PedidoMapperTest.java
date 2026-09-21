package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.response.PedidoResponseDTO;

class PedidoMapperTest {

    private PedidoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PedidoMapperImpl();
        ReflectionTestUtils.setField(mapper, "itemPedidoMapper", new ItemPedidoMapperImpl());
    }

    @Test
    @DisplayName("toResponse - calcula el total sumando los subtotales de los ítems")
    void toResponse_calculaTotalDesdeItems() {
        ItemPedido item1 = ItemPedido.builder().idPlato(1L).nombrePlato("Ajiaco")
                .precioCongelado(20000.0).cantidad(2).build();
        ItemPedido item2 = ItemPedido.builder().idPlato(2L).nombrePlato("Bandeja")
                .precioCongelado(28000.0).cantidad(1).build();
        Pedido pedido = Pedido.builder().id(1L).idMesa(1L)
                .items(List.of(item1, item2)).estado(EstadoPedido.RECIBIDO).build();

        PedidoResponseDTO resultado = mapper.toResponse(pedido);

        assertEquals(68000.0, resultado.getTotal());
        assertEquals(2, resultado.getItems().size());
    }
}
