package com.restaurante.model.domain;

import java.time.LocalDateTime;
import java.util.List;

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
        this.total = items.stream()
                .mapToDouble(ItemPedido::subtotal)
                .sum();
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
