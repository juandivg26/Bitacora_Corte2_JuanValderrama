package com.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.model.entity.PlatoEntity;

public interface IPlatoRepository extends JpaRepository<PlatoEntity, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}
