package com.restaurante.validator;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.Mesa;

public interface ICuentaValidator {

    void validarMesaSinCuentaAbierta(Mesa mesa);

    void validarTransicionEstado(Cuenta cuenta, EstadoCuenta nuevoEstado);
}
