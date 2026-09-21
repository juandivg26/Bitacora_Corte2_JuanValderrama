package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.dto.request.CuentaRequestDTO;
import com.restaurante.model.dto.response.CuentaResponseDTO;

class CuentaMapperTest {

    private final CuentaMapper mapper = new CuentaMapperImpl();

    @Test
    @DisplayName("toDomain - nace ABIERTA y con total en 0")
    void toDomain_asignaEstadoInicial() {
        CuentaRequestDTO dto = new CuentaRequestDTO(1L);

        Cuenta resultado = mapper.toDomain(dto);

        assertEquals(EstadoCuenta.ABIERTA, resultado.getEstado());
        assertEquals(0.0, resultado.getTotal());
    }

    @Test
    @DisplayName("toResponse - mapea todos los campos")
    void toResponse_mapeaTodosLosCampos() {
        Cuenta cuenta = Cuenta.builder().id(1L).idMesa(1L).total(50000.0)
                .estado(EstadoCuenta.EN_PAGO).build();

        CuentaResponseDTO resultado = mapper.toResponse(cuenta);

        assertEquals(50000.0, resultado.getTotal());
        assertEquals(EstadoCuenta.EN_PAGO, resultado.getEstado());
    }
}
