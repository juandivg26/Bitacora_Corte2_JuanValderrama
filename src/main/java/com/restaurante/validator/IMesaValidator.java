package com.restaurante.validator;

import java.util.Collection;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;

public interface IMesaValidator {

    void validarNumeroUnico(Integer numero, Collection<Mesa> mesasExistentes);

    void validarTransicionEstado(Mesa mesa, EstadoMesa nuevoEstado);
}
