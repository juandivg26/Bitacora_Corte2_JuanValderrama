package com.restaurante.model.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "La mesa es obligatoria")
    private Long idMesa;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String cliente;

    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La reserva debe ser a una fecha futura")
    private LocalDateTime fechaHora;

    @NotNull(message = "El número de comensales es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 comensal")
    private Integer comensales;
}
