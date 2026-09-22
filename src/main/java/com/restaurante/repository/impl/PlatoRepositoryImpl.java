package com.restaurante.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.restaurante.model.domain.Plato;
import com.restaurante.repository.IPlatoRepository;

@Repository
public class PlatoRepositoryImpl implements IPlatoRepository {

    private final Map<Long, Plato> platos = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    @Override
    public List<Plato> findAll() {
        return platos.values().stream().toList();
    }

    @Override
    public Optional<Plato> findById(Long id) {
        return Optional.ofNullable(platos.get(id));
    }

    @Override
    public Plato save(Plato plato) {
        if (plato.getId() == null) {
            plato.setId(contador.getAndIncrement());
        }
        platos.put(plato.getId(), plato);
        return plato;
    }

    @Override
    public void deleteById(Long id) {
        platos.remove(id);
    }
}
