package com.restaurante.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.restaurante.model.document.EventoPedidoDocument;
import com.restaurante.model.document.ItemEventoDocument;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.domain.ItemPedido;

/**
 * Traduce entre el objeto de dominio EventoPedido y su documento de MongoDB.
 * Es la "tercera traducción" aplicada al almacenamiento NoSQL.
 */
@Mapper(componentModel = "spring")
public interface EventoPedidoDocumentMapper {

    @Mapping(source = "idPedido", target = "idPedido", qualifiedByName = "uuidToString")
    EventoPedidoDocument toDocument(EventoPedido evento);

    @Mapping(source = "idPedido", target = "idPedido", qualifiedByName = "stringToUuid")
    EventoPedido toDomain(EventoPedidoDocument document);

    @Mapping(target = "id", ignore = true)
    ItemPedido toItemDomain(ItemEventoDocument document);

    ItemEventoDocument toItemDocument(ItemPedido item);

    @Named("uuidToString")
    default String uuidToString(UUID value) {
        return value == null ? null : value.toString();
    }

    @Named("stringToUuid")
    default UUID stringToUuid(String value) {
        return value == null ? null : UUID.fromString(value);
    }
}
