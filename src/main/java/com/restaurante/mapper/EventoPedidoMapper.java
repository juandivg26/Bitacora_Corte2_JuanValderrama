package com.restaurante.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.dto.response.EventoPedidoResponseDTO;

@Mapper(componentModel = "spring", uses = ItemPedidoMapper.class)
public interface EventoPedidoMapper {

    EventoPedidoResponseDTO toResponse(EventoPedido evento);

    List<EventoPedidoResponseDTO> toResponseList(List<EventoPedido> eventos);
}
