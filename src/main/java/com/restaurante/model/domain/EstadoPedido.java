package com.restaurante.model.domain;

public enum EstadoPedido {
    RECIBIDO,
    EN_PREPARACION,
    LISTO,
    ENTREGADO,
    CANCELADO;

    public boolean puedeTransicionarA(EstadoPedido siguiente) {
        return switch (this) {
            case RECIBIDO -> siguiente == EN_PREPARACION || siguiente == CANCELADO;
            case EN_PREPARACION -> siguiente == LISTO || siguiente == CANCELADO;
            case LISTO -> siguiente == ENTREGADO;
            default -> false;
        };
    }

    public boolean esCancelable() {
        return this == RECIBIDO || this == EN_PREPARACION;
    }

    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }
}
