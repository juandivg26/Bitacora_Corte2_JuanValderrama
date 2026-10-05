package com.restaurante.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CalculoUtilsTest {

    @Test
    @DisplayName("subtotal - multiplica el precio congelado por la cantidad")
    void subtotal_multiplicaPrecioPorCantidad() {
        assertEquals(40000.0, CalculoUtils.subtotal(20000.0, 2));
    }

    @Test
    @DisplayName("subtotal - devuelve 0 si falta algun valor")
    void subtotal_conNulos_devuelveCero() {
        assertEquals(0.0, CalculoUtils.subtotal(null, 2));
        assertEquals(0.0, CalculoUtils.subtotal(20000.0, null));
    }

    @Test
    @DisplayName("sumar - ignora los valores nulos")
    void sumar_ignoraNulos() {
        assertEquals(30000.0, CalculoUtils.sumar(Arrays.asList(10000.0, null, 20000.0)));
    }

    @Test
    @DisplayName("sumar - coleccion vacia o nula devuelve 0")
    void sumar_coleccionVacia_devuelveCero() {
        assertEquals(0.0, CalculoUtils.sumar(List.of()));
        assertEquals(0.0, CalculoUtils.sumar(null));
    }

    @Test
    @DisplayName("redondear - aplica HALF_UP a 2 decimales")
    void redondear_dosDecimales() {
        assertEquals(10.13, CalculoUtils.redondear(10.125));
        assertEquals(12.35, CalculoUtils.redondear(12.3456789));
        assertEquals(0.0, CalculoUtils.redondear(null));
    }

    @Test
    @DisplayName("aplicarDescuento - 10% sobre 10000 deja 9000")
    void aplicarDescuento_diezPorCiento_devuelveDescuento() {
        assertEquals(9000.0, CalculoUtils.aplicarDescuento(10000.0, 10.0));
    }

    @Test
    @DisplayName("aplicarDescuento - sin descuento aplicable devuelve el total redondeado")
    void aplicarDescuento_sinDescuento_devuelveTotal() {
        assertEquals(10000.0, CalculoUtils.aplicarDescuento(10000.0, 0.0));
        assertEquals(10000.0, CalculoUtils.aplicarDescuento(10000.0, null));
        assertEquals(0.0, CalculoUtils.aplicarDescuento(null, 10.0));
    }

    @Test
    @DisplayName("aplicarDescuento - un porcentaje mayor a 100 no deja el total en negativo")
    void aplicarDescuento_porcentajeExcesivo_noDaNegativo() {
        assertEquals(0.0, CalculoUtils.aplicarDescuento(10000.0, 150.0));
    }
}
