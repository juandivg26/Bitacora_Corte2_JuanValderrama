package com.restaurante.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Utilidades de texto compartidas (nombres de platos, categorias, filtros de busqueda).
 */
public final class TextoUtils {

    private TextoUtils() {
    }

    /**
     * Normaliza un texto para compararlo: recorta espacios, elimina acentos y pasa a
     * minusculas. Permite comparar "Café" con "cafe" o " PRINCIPALES " con "principales".
     */
    public static String normalizar(String texto) {
        if (texto == null) {
            return null;
        }
        String sinAcentos = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT);
    }

    /**
     * Compara dos textos ignorando mayusculas, acentos y espacios sobrantes.
     * Es la comparacion que usan las busquedas por categoria y la validacion de nombres.
     */
    public static boolean sonIgualesNormalizados(String textoA, String textoB) {
        if (textoA == null || textoB == null) {
            return textoA == null && textoB == null;
        }
        return normalizar(textoA).equals(normalizar(textoB));
    }

    /**
     * Normaliza un nombre para mostrarlo: recorta, colapsa espacios repetidos y deja la
     * primera letra en mayuscula ("  bandeja   paisa " -&gt; "Bandeja paisa").
     *
     * <p>Disponible para la capa de presentacion; el flujo actual guarda el nombre tal
     * como lo envia el cliente.</p>
     */
    public static String normalizarNombre(String nombre) {
        if (esVacio(nombre)) {
            return nombre;
        }
        String limpio = nombre.strip().replaceAll("\\s+", " ");
        return limpio.substring(0, 1).toUpperCase(Locale.ROOT)
                + limpio.substring(1).toLowerCase(Locale.ROOT);
    }

    /**
     * @return true si el texto es nulo o contiene solo espacios.
     */
    public static boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
