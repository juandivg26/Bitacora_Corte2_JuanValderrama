package com.restaurante.validator;

import java.time.LocalDateTime;

import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;

public interface IReservaValidator {

    void validarMesaDisponibleParaReserva(Mesa mesa);

    void validarFechaFutura(LocalDateTime fechaHora);

    void validarPuedeModificarse(Reserva reserva);
}
