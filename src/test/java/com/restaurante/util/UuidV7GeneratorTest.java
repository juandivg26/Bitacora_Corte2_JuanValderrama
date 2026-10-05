package com.restaurante.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UuidV7GeneratorTest {

    @Test
    @DisplayName("generate - produce un UUID con versión 7")
    void generate_version7() {
        UUID uuid = UuidV7Generator.generate();

        assertEquals(7, uuid.version());
    }

    @Test
    @DisplayName("generate - produce un UUID con variante RFC 4122")
    void generate_varianteCorrecta() {
        UUID uuid = UuidV7Generator.generate();

        assertEquals(2, uuid.variant());
    }

    @Test
    @DisplayName("generate - dos llamadas producen UUIDs distintos")
    void generate_dosLlamadas_sonDistintos() {
        UUID primero = UuidV7Generator.generate();
        UUID segundo = UuidV7Generator.generate();

        assertNotNull(primero);
        assertNotEquals(primero, segundo);
    }
}
