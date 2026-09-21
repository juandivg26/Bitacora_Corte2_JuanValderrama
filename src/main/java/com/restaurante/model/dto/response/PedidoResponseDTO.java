package com.restaurante.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.restaurante.model.domain.EstadoPedido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDTO {

    private Long id;
    private Long idMesa;
    private List<ItemPedidoResponseDTO> items;
    private EstadoPedido estado;
    private LocalDateTime timestamp;
    private Double total;
}
