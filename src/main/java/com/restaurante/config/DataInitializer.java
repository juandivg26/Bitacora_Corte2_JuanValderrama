package com.restaurante.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.restaurante.model.domain.Rol;
import com.restaurante.model.entity.UsuarioEntity;
import com.restaurante.repository.IUsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Crea usuarios de prueba con contraseñas conocidas. Solo para local/QA:
 * en PROD se desactiva con APP_SEED_USERS=false (OWASP A5/A7).
 */
@Component
@ConditionalOnProperty(name = "app.seed-users", havingValue = "true", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Inicializando usuarios de prueba predeterminados en la base de datos...");

            crearUsuarioSiNoExiste("admin@americanbites.com", "Admin123*", "Administrador Principal", Rol.ADMINISTRADOR);
            crearUsuarioSiNoExiste("mesero@americanbites.com", "Mesero123*", "Mesero Salón 1", Rol.MESERO);
            crearUsuarioSiNoExiste("cocinero@americanbites.com", "Cocinero123*", "Chef Ejecutivo", Rol.COCINERO);
            crearUsuarioSiNoExiste("cajero@americanbites.com", "Cajero123*", "Cajero Turno A", Rol.CAJERO);
            crearUsuarioSiNoExiste("cliente@americanbites.com", "Cliente123*", "Cliente Frecuente", Rol.CLIENTE);

            log.info("Usuarios inicializados exitosamente.");
        }
    }

    private void crearUsuarioSiNoExiste(String email, String passwordPlano, String nombre, Rol rol) {
        if (!usuarioRepository.existsByEmail(email)) {
            UsuarioEntity usuario = UsuarioEntity.builder()
                    .email(email)
                    .password(passwordEncoder.encode(passwordPlano))
                    .nombre(nombre)
                    .rol(rol)
                    .fechaCreacion(LocalDateTime.now())
                    .build();
            usuarioRepository.save(usuario);
            log.info("Usuario seed creado: email={}, rol={}", email, rol);
        }
    }
}
