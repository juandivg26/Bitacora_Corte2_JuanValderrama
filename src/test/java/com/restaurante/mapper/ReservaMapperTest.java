package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.Reserva;
import com.restaurante.model.dto.request.ReservaRequestDTO;
import com.restaurante.model.dto.response.ReservaResponseDTO;

class ReservaMapperTest {

    private final ReservaMapper mapper = new ReservaMapperImpl();

    @Test
    @DisplayName("toDomain - nace sin cancelar")
    void toDomain_asignaCanceladaFalse() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        ReservaRequestDTO dto = new ReservaRequestDTO(1L, "Juan Pérez", fecha, 4);

        Reserva resultado = mapper.toDomain(dto);

        assertFalse(resultado.getCancelada());
        assertEquals("Juan Pérez", resultado.getCliente());
    }

    @Test
    @DisplayName("toResponse - vigente=true cuando la reserva no está cancelada")
    void toResponse_vigenteTrueSiNoCancelada() {
        Reserva reserva = Reserva.builder().id(1L).idMesa(1L).cliente("Ana")
                .fechaHora(LocalDateTime.now().plusDays(1)).comensales(2).cancelada(false).build();

        ReservaResponseDTO resultado = mapper.toResponse(reserva);

        assertTrue(resultado.getVigente());
    }

    @Test
    @DisplayName("toResponse - vigente=false cuando la reserva está cancelada")
    void toResponse_vigenteFalseSiCancelada() {
        Reserva reserva = Reserva.builder().id(1L).idMesa(1L).cliente("Ana")
                .fechaHora(LocalDateTime.now().plusDays(1)).comensales(2).cancelada(true).build();

        ReservaResponseDTO resultado = mapper.toResponse(reserva);

        assertFalse(resultado.getVigente());
    }
}
