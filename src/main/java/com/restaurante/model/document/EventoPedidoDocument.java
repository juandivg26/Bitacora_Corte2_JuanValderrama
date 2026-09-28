package com.restaurante.model.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.restaurante.model.domain.EstadoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Documento NoSQL (MongoDB) que registra cada cambio de estado de un pedido.
 * Es un log de auditoría: solo se inserta y se consulta, no se actualiza.
 * Incluye un snapshot embebido de los ítems del pedido en ese momento.
 */
@Document(collection = "eventos_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoPedidoDocument {

    @Id
    private String id;

    private String idPedido;
    private Long idMesa;
    private String tipo;
    private EstadoPedido estadoAnterior;
    private EstadoPedido estadoNuevo;
    private LocalDateTime timestamp;
    @Builder.Default
    private List<ItemEventoDocument> items = new ArrayList<>();
}
