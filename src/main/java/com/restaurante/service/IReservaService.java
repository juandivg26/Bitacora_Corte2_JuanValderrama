package com.restaurante.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.restaurante.model.domain.Reserva;

public interface IReservaService {

    List<Reserva> obtenerTodas();

    List<Reserva> obtenerPorMesa(Long idMesa);

    Reserva obtenerPorId(UUID id);

    Reserva crear(Reserva reserva);

    Reserva cancelar(UUID id);

    Reserva reprogramar(UUID id, LocalDateTime nuevaFechaHora);
}
