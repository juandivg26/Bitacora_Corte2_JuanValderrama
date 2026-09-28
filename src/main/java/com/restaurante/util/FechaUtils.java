package com.restaurante.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades de fecha compartidas por los dominios (reservas, pedidos, cuentas).
 * Agrupa en un solo lugar la logica de fechas que antes estaba repetida en los
 * validadores y en los servicios.
 */
public final class FechaUtils {

    /** Formato de presentacion al usuario. */
    private static final DateTimeFormatter FORMATO_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Formato tecnico para logs. */
    private static final DateTimeFormatter FORMATO_ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final String SIN_FECHA = "Sin fecha";

    private FechaUtils() {
    }

    /**
     * @return true si la fecha no es nula y es posterior al instante actual.
     */
    public static boolean esFechaFutura(LocalDateTime fecha) {
        return fecha != null && fecha.isAfter(LocalDateTime.now());
    }

    /**
     * Formatea una fecha para mostrarla al usuario (dd/MM/yyyy HH:mm).
     *
     * @return {@code "Sin fecha"} si la fecha es nula
     */
    public static String formatearFecha(LocalDateTime fecha) {
        return fecha == null ? SIN_FECHA : fecha.format(FORMATO_DISPLAY);
    }

    /**
     * Formatea una fecha en ISO-8601 (yyyy-MM-ddTHH:mm:ss) para logs tecnicos.
     *
     * @return {@code "Sin fecha"} si la fecha es nula
     */
    public static String formatearIso(LocalDateTime fecha) {
        return fecha == null ? SIN_FECHA : fecha.format(FORMATO_ISO);
    }

    /**
     * Indica si dos intervalos [inicio, fin) se solapan.
     * Si falta alguno de los extremos se considera que no se solapan.
     */
    public static boolean seSolapan(LocalDateTime inicioA, LocalDateTime finA,
                                    LocalDateTime inicioB, LocalDateTime finB) {
        if (inicioA == null || finA == null || inicioB == null || finB == null) {
            return false;
        }
        return inicioA.isBefore(finB) && inicioB.isBefore(finA);
    }
}
