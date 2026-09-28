package com.restaurante.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.entity.ItemPedidoEntity;

@Mapper(componentModel = "spring")
public interface ItemPedidoEntityMapper {

    @Mapping(target = "pedido", ignore = true)
    ItemPedidoEntity toEntity(ItemPedido item);

    ItemPedido toDomain(ItemPedidoEntity entity);
}
