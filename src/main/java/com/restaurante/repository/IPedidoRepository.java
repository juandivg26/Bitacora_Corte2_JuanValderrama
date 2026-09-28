package com.restaurante.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.model.entity.PedidoEntity;

public interface IPedidoRepository extends JpaRepository<PedidoEntity, UUID> {

    List<PedidoEntity> findByIdMesa(Long idMesa);
}
