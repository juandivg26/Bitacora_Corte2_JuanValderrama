package com.restaurante.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.restaurante.model.domain.Mesa;
import com.restaurante.repository.IMesaRepository;

@Repository
public class MesaRepositoryImpl implements IMesaRepository {

    private final Map<Long, Mesa> mesas = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    @Override
    public List<Mesa> findAll() {
        return mesas.values().stream().toList();
    }

    @Override
    public Optional<Mesa> findById(Long id) {
        return Optional.ofNullable(mesas.get(id));
    }

    @Override
    public Mesa save(Mesa mesa) {
        if (mesa.getId() == null) {
            mesa.setId(contador.getAndIncrement());
        }
        mesas.put(mesa.getId(), mesa);
        return mesa;
    }

    @Override
    public void deleteById(Long id) {
        mesas.remove(id);
    }
}
