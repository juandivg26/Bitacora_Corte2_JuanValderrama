package com.restaurante.service;

import java.util.List;
import java.util.UUID;

import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.Pedido;

public interface IEventoPedidoService {

    EventoPedido registrarCreacion(Pedido pedido);

    EventoPedido registrarCambioEstado(Pedido pedido, EstadoPedido estadoAnterior, EstadoPedido estadoNuevo);

    List<EventoPedido> obtenerTodos();

    List<EventoPedido> obtenerPorPedido(UUID idPedido);
}
