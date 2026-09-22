package com.restaurante.repository;

import java.util.List;
import java.util.Optional;

import com.restaurante.model.domain.Cuenta;

public interface ICuentaRepository {

    List<Cuenta> findAll();

    Optional<Cuenta> findById(Long id);

    Cuenta save(Cuenta cuenta);
}
