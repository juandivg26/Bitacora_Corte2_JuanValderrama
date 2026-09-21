package com.restaurante.model.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    private Long id;
    private Long idMesa;
    @Builder.Default
    private List<ItemPedido> items = new ArrayList<>();
    private EstadoPedido estado;
    private LocalDateTime timestamp;

    public boolean puedeModificarse() {
        return estado == EstadoPedido.RECIBIDO;
    }

    public void agregarItem(ItemPedido item) {
        this.items.add(item);
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (!this.estado.puedeTransicionarA(nuevoEstado)) {
            throw new IllegalStateException(
                    "No se puede pasar de " + this.estado + " a " + nuevoEstado);
        }
        this.estado = nuevoEstado;
    }

    public Double total() {
        return items.stream()
                .mapToDouble(ItemPedido::subtotal)
                .sum();
    }
}
