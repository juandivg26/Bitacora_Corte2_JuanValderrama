package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.response.ItemPedidoResponseDTO;

class ItemPedidoMapperTest {

    private final ItemPedidoMapper mapper = new ItemPedidoMapperImpl();

    @Test
    @DisplayName("toDomain - no rellena nombrePlato ni precioCongelado (los llena el Service)")
    void toDomain_dejaNombreYPrecioSinRellenar() {
        ItemPedidoRequestDTO dto = new ItemPedidoRequestDTO(1L, 2);

        ItemPedido resultado = mapper.toDomain(dto);

        assertEquals(1L, resultado.getIdPlato());
        assertEquals(2, resultado.getCantidad());
        assertNull(resultado.getNombrePlato());
        assertNull(resultado.getPrecioCongelado());
    }

    @Test
    @DisplayName("toResponse - calcula el subtotal a partir del método de dominio")
    void toResponse_calculaSubtotal() {
        ItemPedido item = ItemPedido.builder().id(1L).idPlato(1L)
                .nombrePlato("Ajiaco").precioCongelado(20000.0).cantidad(3).build();

        ItemPedidoResponseDTO resultado = mapper.toResponse(item);

        assertEquals(60000.0, resultado.getSubtotal());
    }
}
