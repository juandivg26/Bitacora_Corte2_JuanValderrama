package com.restaurante.service;

import java.time.LocalDateTime;
import java.util.List;

import com.restaurante.model.domain.Reserva;

public interface IReservaService {

    List<Reserva> obtenerTodas();

    List<Reserva> obtenerPorMesa(Long idMesa);

    Reserva obtenerPorId(Long id);

    Reserva crear(Reserva reserva);

    Reserva cancelar(Long id);

    Reserva reprogramar(Long id, LocalDateTime nuevaFechaHora);
}
