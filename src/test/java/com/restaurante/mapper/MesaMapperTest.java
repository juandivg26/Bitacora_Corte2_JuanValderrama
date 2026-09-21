package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;

class MesaMapperTest {

    private final MesaMapper mapper = new MesaMapperImpl();

    @Test
    @DisplayName("toDomain - nace DISPONIBLE y sin cuenta abierta")
    void toDomain_asignaEstadoInicial() {
        MesaRequestDTO dto = new MesaRequestDTO(1, 4);

        Mesa resultado = mapper.toDomain(dto);

        assertEquals(EstadoMesa.DISPONIBLE, resultado.getEstado());
        assertFalse(resultado.getCuentaAbierta());
    }

    @Test
    @DisplayName("toResponse - mapea todos los campos")
    void toResponse_mapeaTodosLosCampos() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build();

        MesaResponseDTO resultado = mapper.toResponse(mesa);

        assertEquals(1L, resultado.getId());
        assertEquals(EstadoMesa.OCUPADA, resultado.getEstado());
    }
}
