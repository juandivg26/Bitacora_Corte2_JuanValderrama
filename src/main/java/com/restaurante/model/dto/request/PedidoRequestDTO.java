package com.restaurante.model.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @NotNull(message = "La mesa es obligatoria")
    private Long idMesa;

    @NotEmpty(message = "El pedido debe tener al menos un ítem")
    @Valid
    private List<ItemPedidoRequestDTO> items;
}
