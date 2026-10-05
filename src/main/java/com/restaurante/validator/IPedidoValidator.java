package com.restaurante.validator;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;

public interface IPedidoValidator {

    void validarMesaDisponible(Mesa mesa);

    void validarPlatoDisponible(Plato plato);

    void validarPuedeModificarse(Pedido pedido);

    void validarTransicionEstado(Pedido pedido, EstadoPedido nuevoEstado);
}
