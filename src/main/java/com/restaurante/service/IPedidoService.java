package com.restaurante.service;

import java.util.List;
import java.util.UUID;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;

public interface IPedidoService {

    List<Pedido> obtenerTodos();

    List<Pedido> obtenerPorMesa(Long idMesa);

    Pedido obtenerPorId(UUID id);

    Pedido crear(Pedido pedido);

    Pedido agregarItem(UUID idPedido, ItemPedido item);

    Pedido cambiarEstado(UUID id, EstadoPedido nuevoEstado);
}
