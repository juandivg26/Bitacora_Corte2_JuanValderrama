package com.restaurante.validator.impl;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.validator.IMesaValidator;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MesaValidatorImpl implements IMesaValidator {

    @Override
    public void validarNumeroUnico(Integer numero, Collection<Mesa> mesasExistentes) {
        boolean existe = mesasExistentes.stream()
                .anyMatch(mesa -> mesa.getNumero().equals(numero));
        if (existe) {
            log.warn("Número de mesa duplicado: {}", numero);
            throw new ConflictoException("Ya existe una mesa con el número " + numero);
        }
    }

    @Override
    public void validarTransicionEstado(Mesa mesa, EstadoMesa nuevoEstado) {
        if (!mesa.getEstado().puedeTransicionarA(nuevoEstado)) {
            log.warn("Transición inválida de mesa id={}: {} -> {}", mesa.getId(), mesa.getEstado(), nuevoEstado);
            throw new EstadoInvalidoException(
                    "No se puede pasar la mesa de " + mesa.getEstado() + " a " + nuevoEstado);
        }
    }
}
