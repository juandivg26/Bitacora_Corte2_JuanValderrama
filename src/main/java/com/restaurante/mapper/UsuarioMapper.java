package com.restaurante.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.restaurante.model.domain.Usuario;
import com.restaurante.model.dto.request.RegistroUsuarioRequestDTO;
import com.restaurante.model.dto.response.UsuarioResponseDTO;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Usuario toDomain(RegistroUsuarioRequestDTO dto);

    UsuarioResponseDTO toResponse(Usuario usuario);

    List<UsuarioResponseDTO> toResponseList(List<Usuario> usuarios);
}
