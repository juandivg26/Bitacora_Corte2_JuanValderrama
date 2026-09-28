package com.restaurante.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.model.entity.MesaEntity;

public interface IMesaRepository extends JpaRepository<MesaEntity, Long> {

    boolean existsByNumero(Integer numero);
}
