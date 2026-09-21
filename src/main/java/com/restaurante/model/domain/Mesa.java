package com.restaurante.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesa {

    private Long id;
    private Integer numero;
    private Integer capacidad;
    private EstadoMesa estado;
    private Boolean cuentaAbierta;

    public boolean estaDisponible() {
        return estado == EstadoMesa.DISPONIBLE;
    }

    public void abrirCuenta() {
        this.estado = EstadoMesa.OCUPADA;
        this.cuentaAbierta = true;
    }

    public void cerrarCuenta() {
        this.estado = EstadoMesa.DISPONIBLE;
        this.cuentaAbierta = false;
    }
}
