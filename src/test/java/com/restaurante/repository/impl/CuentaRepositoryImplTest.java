package com.restaurante.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.Cuenta;

class CuentaRepositoryImplTest {

    private final CuentaRepositoryImpl repository = new CuentaRepositoryImpl();

    @Test
    @DisplayName("save - asigna un ID cuando la cuenta es nueva")
    void save_cuentaNueva_asignaId() {
        Cuenta cuenta = Cuenta.builder().idMesa(1L).total(0.0).build();

        Cuenta guardada = repository.save(cuenta);

        assertNotNull(guardada.getId());
    }

    @Test
    @DisplayName("findById - retorna vacío si la cuenta no existe")
    void findById_noExiste_retornaVacio() {
        Optional<Cuenta> resultado = repository.findById(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("findAll - retorna todas las cuentas guardadas")
    void findAll_retornaTodas() {
        repository.save(Cuenta.builder().idMesa(1L).build());
        repository.save(Cuenta.builder().idMesa(2L).build());

        List<Cuenta> resultado = repository.findAll();

        assertEquals(2, resultado.size());
    }
}
