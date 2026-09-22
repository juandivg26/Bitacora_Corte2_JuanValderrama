package com.restaurante.repository;

import java.util.List;
import java.util.Optional;

import com.restaurante.model.domain.Reserva;

public interface IReservaRepository {

    List<Reserva> findAll();

    Optional<Reserva> findById(Long id);

    Reserva save(Reserva reserva);
}
