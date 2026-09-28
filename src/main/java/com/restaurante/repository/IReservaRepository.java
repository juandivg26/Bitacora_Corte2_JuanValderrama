package com.restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.model.entity.ReservaEntity;

public interface IReservaRepository extends JpaRepository<ReservaEntity, UUID> {

    List<ReservaEntity> findByIdMesa(Long idMesa);
}
