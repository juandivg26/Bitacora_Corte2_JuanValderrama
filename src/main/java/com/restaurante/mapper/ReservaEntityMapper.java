package com.restaurante.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.restaurante.model.domain.Reserva;
import com.restaurante.model.entity.ReservaEntity;

@Mapper(componentModel = "spring", uses = MesaEntityResolver.class)
public interface ReservaEntityMapper {

    @Mapping(source = "idMesa", target = "mesa")
    ReservaEntity toEntity(Reserva reserva);

    Reserva toDomain(ReservaEntity entity);
}
