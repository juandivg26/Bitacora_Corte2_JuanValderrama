package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.response.PlatoResponseDTO;

class PlatoMapperTest {

    private final PlatoMapper mapper = new PlatoMapperImpl();

    @Test
    @DisplayName("toDomain - el plato nace disponible y sin ID")
    void toDomain_asignaDisponibleTrueYSinId() {
        PlatoRequestDTO dto = new PlatoRequestDTO("Ajiaco", 20000.0, "SOPAS");

        Plato resultado = mapper.toDomain(dto);

        assertEquals("Ajiaco", resultado.getNombre());
        assertTrue(resultado.getDisponible());
    }

    @Test
    @DisplayName("toResponse - mapea todos los campos del dominio")
    void toResponse_mapeaTodosLosCampos() {
        Plato plato = Plato.builder().id(1L).nombre("Ajiaco").precio(20000.0)
                .categoria("SOPAS").disponible(true).build();

        PlatoResponseDTO resultado = mapper.toResponse(plato);

        assertEquals(1L, resultado.getId());
        assertEquals("Ajiaco", resultado.getNombre());
        assertEquals(20000.0, resultado.getPrecio());
    }

    @Test
    @DisplayName("toResponseList - mapea una lista completa")
    void toResponseList_mapeaLista() {
        Plato p1 = Plato.builder().id(1L).nombre("Ajiaco").build();
        Plato p2 = Plato.builder().id(2L).nombre("Bandeja").build();

        List<PlatoResponseDTO> resultado = mapper.toResponseList(List.of(p1, p2));

        assertEquals(2, resultado.size());
    }
}
