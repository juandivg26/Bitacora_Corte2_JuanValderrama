package com.restaurante.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.restaurante.model.domain.Pedido;
import com.restaurante.model.entity.PedidoEntity;

@Mapper(componentModel = "spring", uses = { ItemPedidoEntityMapper.class, MesaEntityResolver.class })
public interface PedidoEntityMapper {

    @Mapping(source = "idMesa", target = "mesa")
    PedidoEntity toEntity(Pedido pedido);

    Pedido toDomain(PedidoEntity entity);

    @AfterMapping
    default void vincularItemsConPedido(@MappingTarget PedidoEntity entity) {
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setPedido(entity));
        }
    }
}
