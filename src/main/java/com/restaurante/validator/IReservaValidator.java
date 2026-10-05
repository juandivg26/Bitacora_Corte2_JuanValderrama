package com.restaurante.validator;

import java.time.LocalDateTime;
import java.util.List;

import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Reserva;

public interface IReservaValidator {

    void validarMesaDisponibleParaReserva(Mesa mesa);

    void validarFechaFutura(LocalDateTime fechaHora);

    void validarPuedeModificarse(Reserva reserva);

    /**
     * Verifica que la nueva reserva no se solape con otra reserva vigente de la misma mesa.
     *
     * @param idMesa            mesa a reservar (solo para el mensaje de error)
     * @param fechaHora         inicio de la nueva reserva
     * @param reservasDeLaMesa  reservas ya existentes de esa mesa
     */
    void validarSinSolapamiento(Long idMesa, LocalDateTime fechaHora, List<Reserva> reservasDeLaMesa);
}
