package com.restaurante.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.restaurante.model.dto.response.ItemPedidoResponseDTO;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nombrePlato", ignore = true)
    @Mapping(target = "precioCongelado", ignore = true)
    ItemPedido toDomain(ItemPedidoRequestDTO dto);

    @Mapping(target = "subtotal", expression = "java(itemPedido.subtotal())")
    ItemPedidoResponseDTO toResponse(ItemPedido itemPedido);

    List<ItemPedidoResponseDTO> toResponseList(List<ItemPedido> items);
}
