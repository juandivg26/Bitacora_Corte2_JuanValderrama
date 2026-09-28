package com.restaurante.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.restaurante.model.domain.EstadoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoPedidoResponseDTO {

    private String id;
    private UUID idPedido;
    private Long idMesa;
    private String tipo;
    private EstadoPedido estadoAnterior;
    private EstadoPedido estadoNuevo;
    private LocalDateTime timestamp;
    private List<ItemPedidoResponseDTO> items;
}
