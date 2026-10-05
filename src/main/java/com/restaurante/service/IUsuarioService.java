package com.restaurante.service;

import java.util.List;

import com.restaurante.model.domain.Usuario;

public interface IUsuarioService {

    List<Usuario> obtenerTodos();

    Usuario obtenerPorId(Long id);

    Usuario obtenerPorEmail(String email);

    Usuario registrar(Usuario usuario);
}
