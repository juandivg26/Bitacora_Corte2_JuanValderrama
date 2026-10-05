package com.restaurante.model.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento de auditoría del ciclo de vida de un pedido. Pertenece al dominio:
 * no conoce que se almacena en MongoDB (esa traducción la hace el DocumentMapper).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoPedido {

    private String id;
    private UUID idPedido;
    private Long idMesa;
    private String tipo;
    private EstadoPedido estadoAnterior;
    private EstadoPedido estadoNuevo;
    private LocalDateTime timestamp;
    @Builder.Default
    private List<ItemPedido> items = new ArrayList<>();
}
