package com.restaurante.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.restaurante.model.domain.Pedido;
import com.restaurante.repository.IPedidoRepository;

@Repository
public class PedidoRepositoryImpl implements IPedidoRepository {

    private final Map<Long, Pedido> pedidos = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    @Override
    public List<Pedido> findAll() {
        return pedidos.values().stream().toList();
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public Pedido save(Pedido pedido) {
        if (pedido.getId() == null) {
            pedido.setId(contador.getAndIncrement());
        }
        pedidos.put(pedido.getId(), pedido);
        return pedido;
    }
}
