package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.entity.CuentaEntity;

class CuentaEntityMapperTest {

    private CuentaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CuentaEntityMapperImpl();
        ReflectionTestUtils.setField(mapper, "mesaEntityResolver", new MesaEntityResolver());
    }

    @Test
    @DisplayName("toEntity y toDomain - preservan el total, el estado y la mesa")
    void toEntityYToDomain_preservanCampos() {
        Cuenta cuenta = Cuenta.builder().id(1L).idMesa(1L).total(50000.0)
                .estado(EstadoCuenta.EN_PAGO).build();

        CuentaEntity entity = mapper.toEntity(cuenta);
        Cuenta resultado = mapper.toDomain(entity);

        assertEquals(50000.0, resultado.getTotal());
        assertEquals(EstadoCuenta.EN_PAGO, resultado.getEstado());
        assertNotNull(entity.getMesa());
        assertEquals(1L, entity.getMesa().getId());
        assertEquals(1L, resultado.getIdMesa());
    }
}
