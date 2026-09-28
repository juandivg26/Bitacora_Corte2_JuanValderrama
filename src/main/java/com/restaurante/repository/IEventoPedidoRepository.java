package com.restaurante.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.restaurante.model.document.EventoPedidoDocument;

/**
 * Repositorio NoSQL (MongoDB). Se encarga de la coleccion de eventos de pedido.
 */
public interface IEventoPedidoRepository extends MongoRepository<EventoPedidoDocument, String> {

    List<EventoPedidoDocument> findAllByOrderByTimestampDesc();

    List<EventoPedidoDocument> findByIdPedidoOrderByTimestampDesc(String idPedido);
}
