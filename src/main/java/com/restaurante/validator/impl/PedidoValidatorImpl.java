package com.restaurante.validator.impl;

import org.springframework.stereotype.Component;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.validator.IPedidoValidator;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PedidoValidatorImpl implements IPedidoValidator {

    @Override
    public void validarMesaDisponible(Mesa mesa) {
        if (!mesa.estaDisponible()) {
            log.warn("Mesa no disponible para nuevo pedido: id={}, estado={}", mesa.getId(), mesa.getEstado());
            throw new ReglaDeNegocioException(
                    "La mesa " + mesa.getNumero() + " no está disponible (estado: " + mesa.getEstado() + ")");
        }
    }

    @Override
    public void validarPlatoDisponible(Plato plato) {
        if (!plato.estaDisponible()) {
            log.warn("Plato no disponible: id={}, nombre={}", plato.getId(), plato.getNombre());
            throw new ReglaDeNegocioException("El plato '" + plato.getNombre() + "' no está disponible");
        }
    }

    @Override
    public void validarPuedeModificarse(Pedido pedido) {
        if (!pedido.puedeModificarse()) {
            log.warn("Pedido no modificable: id={}, estado={}", pedido.getId(), pedido.getEstado());
            throw new EstadoInvalidoException(
                    "El pedido id=" + pedido.getId() + " no puede modificarse en estado " + pedido.getEstado());
        }
    }

    @Override
    public void validarTransicionEstado(Pedido pedido, EstadoPedido nuevoEstado) {
        if (!pedido.getEstado().puedeTransicionarA(nuevoEstado)) {
            log.warn("Transición inválida de pedido id={}: {} -> {}",
                    pedido.getId(), pedido.getEstado(), nuevoEstado);
            throw new EstadoInvalidoException(
                    "No se puede pasar el pedido de " + pedido.getEstado() + " a " + nuevoEstado);
        }
    }
}
