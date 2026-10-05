package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.entity.ItemPedidoEntity;

class ItemPedidoEntityMapperTest {

    private final ItemPedidoEntityMapper mapper = new ItemPedidoEntityMapperImpl();

    @Test
    @DisplayName("toEntity - deja el campo pedido en null (se enlaza aparte)")
    void toEntity_dejaPedidoEnNull() {
        ItemPedido item = ItemPedido.builder().id(1L).idPlato(1L)
                .nombrePlato("Ajiaco").precioCongelado(20000.0).cantidad(2).build();

        ItemPedidoEntity resultado = mapper.toEntity(item);

        assertNull(resultado.getPedido());
        assertEquals("Ajiaco", resultado.getNombrePlato());
    }

    @Test
    @DisplayName("toDomain - mapea todos los campos")
    void toDomain_mapeaTodosLosCampos() {
        ItemPedidoEntity entity = ItemPedidoEntity.builder().id(1L).idPlato(1L)
                .nombrePlato("Ajiaco").precioCongelado(20000.0).cantidad(2).build();

        ItemPedido resultado = mapper.toDomain(entity);

        assertEquals(2, resultado.getCantidad());
    }
}
