package com.restaurante.service;

import java.util.List;

import com.restaurante.model.domain.Cuenta;

public interface ICuentaService {

    List<Cuenta> obtenerTodas();

    Cuenta obtenerPorId(Long id);

    Cuenta obtenerPorMesa(Long idMesa);

    Cuenta abrir(Long idMesa);

    Cuenta registrarPago(Long id);

    Cuenta cerrar(Long id);
}
