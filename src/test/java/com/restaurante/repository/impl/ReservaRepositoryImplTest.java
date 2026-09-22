package com.restaurante.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.Reserva;

class ReservaRepositoryImplTest {

    private final ReservaRepositoryImpl repository = new ReservaRepositoryImpl();

    @Test
    @DisplayName("save - asigna un ID cuando la reserva es nueva")
    void save_reservaNueva_asignaId() {
        Reserva reserva = Reserva.builder().idMesa(1L).cliente("Juan").build();

        Reserva guardada = repository.save(reserva);

        assertNotNull(guardada.getId());
    }

    @Test
    @DisplayName("findById - retorna vacío si la reserva no existe")
    void findById_noExiste_retornaVacio() {
        Optional<Reserva> resultado = repository.findById(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findAll - retorna todas las reservas guardadas")
    void findAll_retornaTodas() {
        repository.save(Reserva.builder().idMesa(1L).cliente("Juan").build());
        repository.save(Reserva.builder().idMesa(2L).cliente("Ana").build());

        List<Reserva> resultado = repository.findAll();

        assertEquals(2, resultado.size());
    }
}
