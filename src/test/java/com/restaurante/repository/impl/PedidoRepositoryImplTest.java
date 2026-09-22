package com.restaurante.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.Pedido;

class PedidoRepositoryImplTest {

    private final PedidoRepositoryImpl repository = new PedidoRepositoryImpl();

    @Test
    @DisplayName("save - asigna un ID cuando el pedido es nuevo")
    void save_pedidoNuevo_asignaId() {
        Pedido pedido = Pedido.builder().idMesa(1L).build();

        Pedido guardado = repository.save(pedido);

        assertNotNull(guardado.getId());
    }

    @Test
    @DisplayName("findById - retorna vacío si el pedido no existe")
    void findById_noExiste_retornaVacio() {
        Optional<Pedido> resultado = repository.findById(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findAll - retorna todos los pedidos guardados")
    void findAll_retornaTodos() {
        repository.save(Pedido.builder().idMesa(1L).build());
        repository.save(Pedido.builder().idMesa(2L).build());

        List<Pedido> resultado = repository.findAll();

        assertEquals(2, resultado.size());
    }
}
