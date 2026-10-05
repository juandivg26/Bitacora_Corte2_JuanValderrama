package com.restaurante.mapper;

import org.mapstruct.Mapper;

import com.restaurante.model.domain.Usuario;
import com.restaurante.model.entity.UsuarioEntity;

@Mapper(componentModel = "spring")
public interface UsuarioEntityMapper {

    UsuarioEntity toEntity(Usuario usuario);

    Usuario toDomain(UsuarioEntity entity);
}
