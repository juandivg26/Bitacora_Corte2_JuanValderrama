package com.restaurante.validator;

import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;

public interface IMesaValidator {

    void validarNumeroUnico(Integer numero);

    void validarTransicionEstado(Mesa mesa, EstadoMesa nuevoEstado);
}
