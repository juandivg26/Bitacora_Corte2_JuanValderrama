package com.restaurante.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    /**
     * Relación con la mesa: es la fuente de escritura de la FK (id_mesa).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mesa", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MesaEntity mesa;

    /**
     * La misma columna FK en modo solo lectura.
     */
    @Column(name = "id_mesa", insertable = false, updatable = false)
    private Long idMesa;

    @Column(nullable = false)
    private String cliente;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private Integer comensales;

    @Column(nullable = false)
    private Boolean cancelada;
}
