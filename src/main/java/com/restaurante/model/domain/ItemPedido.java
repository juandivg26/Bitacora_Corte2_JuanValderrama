package com.restaurante.model.domain;

import com.restaurante.util.CalculoUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {

    private Long id;
    private Long idPlato;
    private String nombrePlato;
    private Double precioCongelado;
    private Integer cantidad;

    public Double subtotal() {
        return CalculoUtils.subtotal(precioCongelado, cantidad);
    }
}
