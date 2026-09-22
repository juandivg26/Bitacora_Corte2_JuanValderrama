package com.restaurante.repository;

import java.util.List;
import java.util.Optional;

import com.restaurante.model.domain.Pedido;

public interface IPedidoRepository {

    List<Pedido> findAll();

    Optional<Pedido> findById(Long id);

    Pedido save(Pedido pedido);
}
