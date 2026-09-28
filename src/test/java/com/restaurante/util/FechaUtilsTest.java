package com.restaurante.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FechaUtilsTest {

    @Test
    @DisplayName("esFechaFutura - una fecha futura devuelve true")
    void esFechaFutura_fechaFutura_devuelveTrue() {
        assertTrue(FechaUtils.esFechaFutura(LocalDateTime.now().plusDays(1)));
    }

    @Test
    @DisplayName("esFechaFutura - fecha pasada o nula devuelve false")
    void esFechaFutura_fechaPasadaONula_devuelveFalse() {
        assertFalse(FechaUtils.esFechaFutura(LocalDateTime.now().minusDays(1)));
        assertFalse(FechaUtils.esFechaFutura(null));
    }

    @Test
    @DisplayName("formatearFecha - usa dd/MM/yyyy HH:mm y 'Sin fecha' para nulos")
    void formatearFecha_usaFormatoDisplay() {
        assertEquals("15/03/2026 18:30", FechaUtils.formatearFecha(LocalDateTime.of(2026, 3, 15, 18, 30)));
        assertEquals("Sin fecha", FechaUtils.formatearFecha(null));
    }

    @Test
    @DisplayName("formatearIso - usa ISO-8601 para los logs")
    void formatearIso_usaFormatoIso() {
        assertEquals("2026-03-15T18:30:00", FechaUtils.formatearIso(LocalDateTime.of(2026, 3, 15, 18, 30)));
        assertEquals("Sin fecha", FechaUtils.formatearIso(null));
    }

    @Test
    @DisplayName("seSolapan - detecta intervalos cruzados")
    void seSolapan_intervalosCruzados_devuelveTrue() {
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 15, 18, 0);

        assertTrue(FechaUtils.seSolapan(inicio, inicio.plusHours(2),
                inicio.plusHours(1), inicio.plusHours(3)));
    }

    @Test
    @DisplayName("seSolapan - intervalos contiguos no se solapan")
    void seSolapan_intervalosContiguos_devuelveFalse() {
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 15, 18, 0);

        assertFalse(FechaUtils.seSolapan(inicio, inicio.plusHours(2),
                inicio.plusHours(2), inicio.plusHours(4)));
    }

    @Test
    @DisplayName("seSolapan - con extremos nulos devuelve false")
    void seSolapan_conNulos_devuelveFalse() {
        assertFalse(FechaUtils.seSolapan(null, null, null, null));
    }
}
