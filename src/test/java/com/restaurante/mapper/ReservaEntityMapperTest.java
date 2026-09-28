package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.restaurante.model.domain.Reserva;
import com.restaurante.model.entity.ReservaEntity;

class ReservaEntityMapperTest {

    private ReservaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ReservaEntityMapperImpl();
        ReflectionTestUtils.setField(mapper, "mesaEntityResolver", new MesaEntityResolver());
    }

    @Test
    @DisplayName("toEntity y toDomain - preservan cliente, fecha y mesa")
    void toEntityYToDomain_preservanCampos() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        Reserva reserva = Reserva.builder().id(UUID.randomUUID()).idMesa(1L).cliente("Juan Pérez")
                .fechaHora(fecha).comensales(4).cancelada(false).build();

        ReservaEntity entity = mapper.toEntity(reserva);
        Reserva resultado = mapper.toDomain(entity);

        assertEquals("Juan Pérez", resultado.getCliente());
        assertEquals(fecha, resultado.getFechaHora());
        assertNotNull(entity.getMesa());
        assertEquals(1L, entity.getMesa().getId());
        assertEquals(1L, resultado.getIdMesa());
    }
}
