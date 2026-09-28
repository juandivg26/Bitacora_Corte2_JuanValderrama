package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.entity.PedidoEntity;

class PedidoEntityMapperTest {

    private PedidoEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PedidoEntityMapperImpl();
        ReflectionTestUtils.setField(mapper, "itemPedidoEntityMapper", new ItemPedidoEntityMapperImpl());
        ReflectionTestUtils.setField(mapper, "mesaEntityResolver", new MesaEntityResolver());
    }

    @Test
    @DisplayName("toEntity - enlaza cada ítem con su pedido padre y resuelve la relación con la mesa")
    void toEntity_enlazaItemsConPedido() {
        ItemPedido item = ItemPedido.builder().idPlato(1L).nombrePlato("Ajiaco")
                .precioCongelado(20000.0).cantidad(2).build();
        Pedido pedido = Pedido.builder().idMesa(1L).items(List.of(item))
                .estado(EstadoPedido.RECIBIDO).timestamp(LocalDateTime.now()).build();

        PedidoEntity resultado = mapper.toEntity(pedido);

        assertEquals(1, resultado.getItems().size());
        assertSame(resultado, resultado.getItems().get(0).getPedido());
        // La relación @ManyToOne a la mesa se resuelve desde el idMesa del dominio
        assertNotNull(resultado.getMesa());
        assertEquals(1L, resultado.getMesa().getId());
        assertEquals(1L, resultado.getIdMesa());
    }

    @Test
    @DisplayName("toDomain - recupera el idMesa desde el campo de solo lectura")
    void toDomain_recuperaIdMesa() {
        PedidoEntity entity = PedidoEntity.builder()
                .idMesa(7L)
                .estado(EstadoPedido.RECIBIDO)
                .timestamp(LocalDateTime.now())
                .build();

        Pedido resultado = mapper.toDomain(entity);

        assertEquals(7L, resultado.getIdMesa());
    }
}
