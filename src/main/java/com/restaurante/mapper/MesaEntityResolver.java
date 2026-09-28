package com.restaurante.mapper;

import org.springframework.stereotype.Component;

import com.restaurante.model.entity.MesaEntity;

/**
 * Construye la referencia a la mesa (solo con su id) para poblar relaciones
 * @ManyToOne sin necesidad de cargar la entidad completa desde la base de datos.
 */
@Component
public class MesaEntityResolver {

    public MesaEntity toMesaEntity(Long idMesa) {
        if (idMesa == null) {
            return null;
        }
        return MesaEntity.builder().id(idMesa).build();
    }
}
