package com.restaurante.service;

import java.util.List;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;

public interface IPedidoService {

    List<Pedido> obtenerTodos();

    List<Pedido> obtenerPorMesa(Long idMesa);

    Pedido obtenerPorId(Long id);

    Pedido crear(Pedido pedido);

    Pedido agregarItem(Long idPedido, ItemPedido item);

    Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado);
}
