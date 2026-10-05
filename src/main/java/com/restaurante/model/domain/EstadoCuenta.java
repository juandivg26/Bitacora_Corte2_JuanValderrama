package com.restaurante.model.domain;

public enum EstadoCuenta {
    ABIERTA,
    EN_PAGO,
    CERRADA;

    public boolean puedeTransicionarA(EstadoCuenta siguiente) {
        return switch (this) {
            case ABIERTA -> siguiente == EN_PAGO;
            case EN_PAGO -> siguiente == CERRADA || siguiente == ABIERTA;
            case CERRADA -> false;
        };
    }
}
