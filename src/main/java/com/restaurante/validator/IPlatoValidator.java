package com.restaurante.validator;

import java.util.Collection;

import com.restaurante.model.domain.Plato;

public interface IPlatoValidator {

    void validarNombreUnico(String nombre, Collection<Plato> platosExistentes);
}
