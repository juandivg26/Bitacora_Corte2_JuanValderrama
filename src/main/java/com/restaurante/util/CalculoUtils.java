package com.restaurante.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/**
 * Utilidades de calculo monetario compartidas por los dominios de pedidos y cuentas.
 * Centraliza el subtotal, la sumatoria y el redondeo para que el total siempre se
 * calcule de la misma forma.
 */
public final class CalculoUtils {

    private static final int DECIMALES_MONEDA = 2;

    private CalculoUtils() {
    }

    /**
     * Subtotal de una linea de pedido: precio unitario congelado x cantidad.
     * Devuelve 0.0 si falta alguno de los dos valores.
     */
    public static Double subtotal(Double precioUnitario, Integer cantidad) {
        if (precioUnitario == null || cantidad == null) {
            return 0.0;
        }
        return redondear(precioUnitario * cantidad);
    }

    /**
     * Suma segura de una coleccion de valores: ignora los nulos.
     */
    public static Double sumar(Collection<Double> valores) {
        if (valores == null || valores.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Double valor : valores) {
            if (valor != null) {
                total += valor;
            }
        }
        return redondear(total);
    }

    /**
     * Redondea a 2 decimales con HALF_UP, el estandar para valores monetarios.
     */
    public static Double redondear(Double valor) {
        if (valor == null) {
            return 0.0;
        }
        return BigDecimal.valueOf(valor).setScale(DECIMALES_MONEDA, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Aplica un porcentaje de descuento a un total (0-100).
     *
     * <p>Disponible para promociones sobre cuentas o reservas; el flujo actual todavia
     * no aplica descuentos, por eso no se invoca desde los servicios.</p>
     */
    public static Double aplicarDescuento(Double total, Double porcentaje) {
        if (total == null || total <= 0 || porcentaje == null || porcentaje <= 0) {
            return redondear(total);
        }
        double porcentajeAplicable = Math.min(porcentaje, 100.0);
        return redondear(total * (1 - porcentajeAplicable / 100.0));
    }
}
