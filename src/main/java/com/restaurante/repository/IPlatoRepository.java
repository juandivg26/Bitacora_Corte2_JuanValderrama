package com.restaurante.repository;

import java.util.List;
import java.util.Optional;

import com.restaurante.model.domain.Plato;

public interface IPlatoRepository {

    List<Plato> findAll();

    Optional<Plato> findById(Long id);

    Plato save(Plato plato);

    void deleteById(Long id);
}
