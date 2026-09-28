package com.restaurante.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.restaurante.model.domain.Mesa;
import com.restaurante.model.dto.request.MesaRequestDTO;
import com.restaurante.model.dto.response.MesaResponseDTO;

@Mapper(componentModel = "spring")
public interface MesaMapper {

    Mesa toDomain(MesaRequestDTO dto);

    MesaResponseDTO toResponse(Mesa mesa);

    List<MesaResponseDTO> toResponseList(List<Mesa> mesas);
}
