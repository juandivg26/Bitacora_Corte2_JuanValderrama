package com.restaurante.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.restaurante.model.domain.Reserva;
import com.restaurante.repository.IReservaRepository;

@Repository
public class ReservaRepositoryImpl implements IReservaRepository {

    private final Map<Long, Reserva> reservas = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    @Override
    public List<Reserva> findAll() {
        return reservas.values().stream().toList();
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        return Optional.ofNullable(reservas.get(id));
    }

    @Override
    public Reserva save(Reserva reserva) {
        if (reserva.getId() == null) {
            reserva.setId(contador.getAndIncrement());
        }
        reservas.put(reserva.getId(), reserva);
        return reserva;
    }
}
