package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.domain.Plato;
import com.restaurante.model.entity.PlatoEntity;

class PlatoEntityMapperTest {

    private final PlatoEntityMapper mapper = new PlatoEntityMapperImpl();

    @Test
    @DisplayName("toEntity - mapea todos los campos del dominio a la entidad")
    void toEntity_mapeaTodosLosCampos() {
        Plato plato = Plato.builder().id(1L).nombre("Ajiaco").precio(20000.0)
                .categoria("SOPAS").disponible(true).build();

        PlatoEntity resultado = mapper.toEntity(plato);

        assertEquals(1L, resultado.getId());
        assertEquals("Ajiaco", resultado.getNombre());
        assertEquals(20000.0, resultado.getPrecio());
    }

    @Test
    @DisplayName("toDomain - mapea todos los campos de la entidad al dominio")
    void toDomain_mapeaTodosLosCampos() {
        PlatoEntity entity = PlatoEntity.builder().id(1L).nombre("Ajiaco")
                .precio(20000.0).categoria("SOPAS").disponible(true).build();

        Plato resultado = mapper.toDomain(entity);

        assertEquals("Ajiaco", resultado.getNombre());
        assertEquals("SOPAS", resultado.getCategoria());
    }
}
