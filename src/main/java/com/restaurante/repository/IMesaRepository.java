package com.restaurante.repository;

import java.util.List;
import java.util.Optional;

import com.restaurante.model.domain.Mesa;

public interface IMesaRepository {

    List<Mesa> findAll();

    Optional<Mesa> findById(Long id);

    Mesa save(Mesa mesa);

    void deleteById(Long id);
}
