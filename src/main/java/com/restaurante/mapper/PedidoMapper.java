package com.restaurante.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.dto.request.PedidoRequestDTO;
import com.restaurante.model.dto.response.PedidoResponseDTO;

@Mapper(componentModel = "spring", uses = ItemPedidoMapper.class)
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", constant = "RECIBIDO")
    @Mapping(target = "timestamp", ignore = true)
    Pedido toDomain(PedidoRequestDTO dto);

    @Mapping(target = "total", expression = "java(pedido.total())")
    PedidoResponseDTO toResponse(Pedido pedido);

    List<PedidoResponseDTO> toResponseList(List<Pedido> pedidos);
}
