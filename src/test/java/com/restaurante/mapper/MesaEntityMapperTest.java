package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.entity.MesaEntity;

class MesaEntityMapperTest {

    private final MesaEntityMapper mapper = new MesaEntityMapperImpl();

    @Test
    @DisplayName("toEntity y toDomain - preservan el estado de la mesa")
    void toEntityYToDomain_preservanEstado() {
        Mesa mesa = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.OCUPADA).cuentaAbierta(true).build();

        MesaEntity entity = mapper.toEntity(mesa);
        Mesa resultado = mapper.toDomain(entity);

        assertEquals(EstadoMesa.OCUPADA, resultado.getEstado());
        assertEquals(1, resultado.getNumero());
    }
}
