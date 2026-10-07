package com.restaurante.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.restaurante.model.domain.Rol;
import com.restaurante.model.entity.UsuarioEntity;
import com.restaurante.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final IUsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioEntity usuario = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado para autenticación: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        List<GrantedAuthority> authorities = mapAuthorities(usuario.getRol());

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(authorities)
                .build();
    }

    public static List<GrantedAuthority> mapAuthorities(Rol rol) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (rol == null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CLIENTE"));
            return authorities;
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.name()));

        // Alias comunes para interoperabilidad
        if (rol == Rol.ADMINISTRADOR) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        // NOTA: COCINERO NO tiene ROLE_CHEF para evitar que pueda crear/editar platos
        // Solo ADMINISTRADOR puede gestionar platos según la matriz de roles

        return authorities;
    }
}
