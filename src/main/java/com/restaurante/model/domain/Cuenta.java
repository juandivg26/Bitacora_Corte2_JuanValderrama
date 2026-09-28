package com.restaurante.model.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.restaurante.util.CalculoUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {

    private Long id;
    private Long idMesa;
    private Double total;
    private EstadoCuenta estado;
    private LocalDateTime fechaApertura;

    public Double calcularTotal(List<ItemPedido> items) {
        if (items == null || items.isEmpty()) {
            this.total = 0.0;
            return this.total;
        }
        this.total = CalculoUtils.sumar(items.stream()
                .map(ItemPedido::subtotal)
                .toList());
        return this.total;
    }

    public void registrarPago() {
        cambiarEstado(EstadoCuenta.EN_PAGO);
    }

    public void cerrarCuenta() {
        cambiarEstado(EstadoCuenta.CERRADA);
    }

    private void cambiarEstado(EstadoCuenta nuevoEstado) {
        if (!this.estado.puedeTransicionarA(nuevoEstado)) {
            throw new IllegalStateException(
                    "No se puede pasar de " + this.estado + " a " + nuevoEstado);
        }
        this.estado = nuevoEstado;
    }
}
