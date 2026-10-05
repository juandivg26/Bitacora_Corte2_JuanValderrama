package com.restaurante.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.entity.CuentaEntity;

@Mapper(componentModel = "spring", uses = MesaEntityResolver.class)
public interface CuentaEntityMapper {

    @Mapping(source = "idMesa", target = "mesa")
    CuentaEntity toEntity(Cuenta cuenta);

    Cuenta toDomain(CuentaEntity entity);
}
