package com.restaurante.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.Mesa;

class MesaRepositoryImplTest {

    private final MesaRepositoryImpl repository = new MesaRepositoryImpl();

    @Test
    @DisplayName("save - asigna un ID cuando la mesa es nueva")
    void save_mesaNueva_asignaId() {
        Mesa mesa = Mesa.builder().numero(1).capacidad(4).build();

        Mesa guardada = repository.save(mesa);

        assertNotNull(guardada.getId());
    }

    @Test
    @DisplayName("findById - retorna vacío si la mesa no existe")
    void findById_noExiste_retornaVacio() {
        Optional<Mesa> resultado = repository.findById(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findAll - retorna todas las mesas guardadas")
    void findAll_retornaTodas() {
        repository.save(Mesa.builder().numero(1).capacidad(4).build());
        repository.save(Mesa.builder().numero(2).capacidad(2).build());

        List<Mesa> resultado = repository.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("deleteById - elimina la mesa guardada")
    void deleteById_eliminaMesa() {
        Mesa guardada = repository.save(Mesa.builder().numero(1).capacidad(4).build());

        repository.deleteById(guardada.getId());

        assertFalse(repository.findById(guardada.getId()).isPresent());
    }
}
