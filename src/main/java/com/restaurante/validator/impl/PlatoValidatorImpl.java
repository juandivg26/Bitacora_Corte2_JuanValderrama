package com.restaurante.validator.impl;

import java.util.Collection;

import org.springframework.stereotype.Component;

import com.restaurante.exception.ConflictoException;
import com.restaurante.model.domain.Plato;
import com.restaurante.validator.IPlatoValidator;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PlatoValidatorImpl implements IPlatoValidator {

    @Override
    public void validarNombreUnico(String nombre, Collection<Plato> platosExistentes) {
        boolean existe = platosExistentes.stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre));
        if (existe) {
            log.warn("Nombre de plato duplicado: {}", nombre);
            throw new ConflictoException("Ya existe un plato con el nombre '" + nombre + "'");
        }
    }
}
