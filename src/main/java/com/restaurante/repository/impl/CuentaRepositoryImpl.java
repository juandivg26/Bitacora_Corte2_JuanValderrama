package com.restaurante.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.restaurante.model.domain.Cuenta;
import com.restaurante.repository.ICuentaRepository;

@Repository
public class CuentaRepositoryImpl implements ICuentaRepository {

    private final Map<Long, Cuenta> cuentas = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    @Override
    public List<Cuenta> findAll() {
        return cuentas.values().stream().toList();
    }

    @Override
    public Optional<Cuenta> findById(Long id) {
        return Optional.ofNullable(cuentas.get(id));
    }

    @Override
    public Cuenta save(Cuenta cuenta) {
        if (cuenta.getId() == null) {
            cuenta.setId(contador.getAndIncrement());
        }
        cuentas.put(cuenta.getId(), cuenta);
        return cuenta;
    }
}
