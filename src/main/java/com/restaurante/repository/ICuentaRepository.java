package com.restaurante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.entity.CuentaEntity;

public interface ICuentaRepository extends JpaRepository<CuentaEntity, Long> {

    List<CuentaEntity> findByIdMesa(Long idMesa);
    
    List<CuentaEntity> findByIdMesaAndEstadoNot(Long idMesa, EstadoCuenta estado);
}
