package com.restaurante.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.Plato;

class PlatoRepositoryImplTest {

    private final PlatoRepositoryImpl repository = new PlatoRepositoryImpl();

    @Test
    @DisplayName("save - asigna un ID cuando el plato es nuevo")
    void save_platoNuevo_asignaId() {
        Plato plato = Plato.builder().nombre("Ajiaco").precio(20000.0).categoria("SOPAS").build();

        Plato guardado = repository.save(plato);

        assertNotNull(guardado.getId());
    }

    @Test
    @DisplayName("save - conserva el ID cuando el plato ya lo tenía")
    void save_platoExistente_conservaId() {
        Plato guardado = repository.save(Plato.builder().nombre("Ajiaco").build());
        Long idOriginal = guardado.getId();

        guardado.setNombre("Ajiaco actualizado");
        Plato actualizado = repository.save(guardado);

        assertEquals(idOriginal, actualizado.getId());
    }

    @Test
    @DisplayName("findById - retorna vacío si el plato no existe")
    void findById_noExiste_retornaVacio() {
        Optional<Plato> resultado = repository.findById(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findAll - retorna todos los platos guardados")
    void findAll_retornaTodos() {
        repository.save(Plato.builder().nombre("Ajiaco").build());
        repository.save(Plato.builder().nombre("Bandeja").build());

        List<Plato> resultado = repository.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("deleteById - elimina el plato guardado")
    void deleteById_eliminaPlato() {
        Plato guardado = repository.save(Plato.builder().nombre("Ajiaco").build());

        repository.deleteById(guardado.getId());

        assertFalse(repository.findById(guardado.getId()).isPresent());
    }
}
