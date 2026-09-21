package com.restaurante.validator.impl;

import org.springframework.stereotype.Component;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.Mesa;
import com.restaurante.validator.ICuentaValidator;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CuentaValidatorImpl implements ICuentaValidator {

    @Override
    public void validarMesaSinCuentaAbierta(Mesa mesa) {
        if (Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            log.warn("La mesa ya tiene cuenta abierta: id={}", mesa.getId());
            throw new ReglaDeNegocioException(
                    "La mesa " + mesa.getNumero() + " ya tiene una cuenta abierta");
        }
    }

    @Override
    public void validarTransicionEstado(Cuenta cuenta, EstadoCuenta nuevoEstado) {
        if (!cuenta.getEstado().puedeTransicionarA(nuevoEstado)) {
            log.warn("Transición inválida de cuenta id={}: {} -> {}",
                    cuenta.getId(), cuenta.getEstado(), nuevoEstado);
            throw new EstadoInvalidoException(
                    "No se puede pasar la cuenta de " + cuenta.getEstado() + " a " + nuevoEstado);
        }
    }
}
