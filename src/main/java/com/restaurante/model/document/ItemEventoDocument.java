package com.restaurante.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ítem embebido dentro del documento de evento.
 * Se embebe (en lugar de referenciar) porque siempre se consulta junto con el
 * evento, la relación es 1 a pocos y el dato no cambia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemEventoDocument {

    private Long idPlato;
    private String nombrePlato;
    private Double precioCongelado;
    private Integer cantidad;
}
