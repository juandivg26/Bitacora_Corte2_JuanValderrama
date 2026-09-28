package com.restaurante.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TextoUtilsTest {

    @Test
    @DisplayName("normalizar - recorta, quita acentos y pasa a minusculas")
    void normalizar_limpiaElTexto() {
        assertEquals("cafe con leche", TextoUtils.normalizar("  Café con Leche "));
    }

    @Test
    @DisplayName("normalizar - texto nulo devuelve nulo")
    void normalizar_nulo_devuelveNulo() {
        assertNull(TextoUtils.normalizar(null));
    }

    @Test
    @DisplayName("sonIgualesNormalizados - ignora mayusculas, acentos y espacios")
    void sonIgualesNormalizados_ignoraFormato() {
        assertTrue(TextoUtils.sonIgualesNormalizados("PRINCIPALES", " principales "));
        assertTrue(TextoUtils.sonIgualesNormalizados("Café", "cafe"));
        assertFalse(TextoUtils.sonIgualesNormalizados("SOPAS", "ENTRADAS"));
    }

    @Test
    @DisplayName("sonIgualesNormalizados - dos nulos son iguales, un solo nulo no")
    void sonIgualesNormalizados_conNulos() {
        assertTrue(TextoUtils.sonIgualesNormalizados(null, null));
        assertFalse(TextoUtils.sonIgualesNormalizados(null, "algo"));
    }

    @Test
    @DisplayName("normalizarNombre - colapsa espacios y capitaliza la primera letra")
    void normalizarNombre_limpiaElNombre() {
        assertEquals("Bandeja paisa", TextoUtils.normalizarNombre("  bandeja   paisa "));
        assertEquals("Ajiaco", TextoUtils.normalizarNombre("AJIACO"));
    }

    @Test
    @DisplayName("normalizarNombre - nulo o en blanco no revienta")
    void normalizarNombre_conVacios_noRevienta() {
        assertNull(TextoUtils.normalizarNombre(null));
        assertEquals("   ", TextoUtils.normalizarNombre("   "));
    }

    @Test
    @DisplayName("esVacio - detecta nulos y cadenas en blanco")
    void esVacio_detectaVacios() {
        assertTrue(TextoUtils.esVacio(null));
        assertTrue(TextoUtils.esVacio("   "));
        assertFalse(TextoUtils.esVacio("Ajiaco"));
    }
}
