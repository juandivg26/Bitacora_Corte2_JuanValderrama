package com.restaurante.model.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    private Long id;
    private String email;
    private String password;
    private String nombre;
    private Rol rol;
    private LocalDateTime fechaCreacion;
}
