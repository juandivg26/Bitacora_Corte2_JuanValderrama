package com.restaurante.validator.impl;

import org.springframework.stereotype.Component;

import com.restaurante.exception.ConflictoException;
import com.restaurante.repository.IPlatoRepository;
import com.restaurante.validator.IPlatoValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PlatoValidatorImpl implements IPlatoValidator {

    private final IPlatoRepository platoRepository;

    @Override
    public void validarNombreUnico(String nombre) {
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            log.warn("Nombre de plato duplicado: {}", nombre);
            throw new ConflictoException("Ya existe un plato con el nombre '" + nombre + "'");
        }
    }
}
