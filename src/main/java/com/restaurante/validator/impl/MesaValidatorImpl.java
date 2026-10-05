package com.restaurante.validator.impl;

import org.springframework.stereotype.Component;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.Mesa;
import com.restaurante.repository.IMesaRepository;
import com.restaurante.validator.IMesaValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class MesaValidatorImpl implements IMesaValidator {

    private final IMesaRepository mesaRepository;

    @Override
    public void validarNumeroUnico(Integer numero) {
        if (mesaRepository.existsByNumero(numero)) {
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
