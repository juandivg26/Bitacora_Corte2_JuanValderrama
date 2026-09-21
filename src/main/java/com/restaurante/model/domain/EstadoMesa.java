package com.restaurante.model.domain;

public enum EstadoMesa {
    DISPONIBLE,
    OCUPADA,
    RESERVADA;

    public boolean puedeTransicionarA(EstadoMesa siguiente) {
        return switch (this) {
            case DISPONIBLE -> siguiente == OCUPADA || siguiente == RESERVADA;
            case OCUPADA -> siguiente == DISPONIBLE;
            case RESERVADA -> siguiente == OCUPADA || siguiente == DISPONIBLE;
        };
    }
}
