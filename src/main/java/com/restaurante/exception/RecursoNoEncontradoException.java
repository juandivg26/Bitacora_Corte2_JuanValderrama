package com.restaurante.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super("No existe " + recurso + " con id=" + id);
    }
}
