package com.restaurante.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.restaurante.security.handler.SecurityAccessDeniedHandler;
import com.restaurante.security.handler.SecurityEntryPoint;
import com.restaurante.security.jwt.JwtAuthFilter;
import com.restaurante.security.oauth2.OAuth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final SecurityEntryPoint securityEntryPoint;
    private final SecurityAccessDeniedHandler securityAccessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF (API REST stateless con JWT)
                .csrf(csrf -> csrf.disable())

                // 2. Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 3. Headers de seguridad HTTP (XSS, Clickjacking, CSP)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:;"))
                )

                // 4. Manejo de excepciones de seguridad con respuestas JSON uniformes
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(securityEntryPoint)
                        .accessDeniedHandler(securityAccessDeniedHandler)
                )

                // 5. Política de sesiones: STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 6. Matriz de autorización por URL (trazable a Roles_AmericanBites.xlsx)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/menu/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/error"
                        ).permitAll()

                        // Módulo Platos
                        .requestMatchers(HttpMethod.GET, "/api/v1/platos/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "COCINERO", "CHEF", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/v1/platos/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/platos/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/platos/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/platos/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "CHEF")

                        // Módulo Mesas
                        .requestMatchers(HttpMethod.GET, "/api/v1/mesas/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "COCINERO", "CHEF", "CAJERO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/mesas/*/estado")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.POST, "/api/v1/mesas/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/mesas/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN")

                        // Módulo Pedidos
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "COCINERO", "CHEF", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/*/estado")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "COCINERO", "CHEF")

                        // Módulo Cuentas
                        .requestMatchers(HttpMethod.GET, "/api/v1/cuentas/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/v1/cuentas/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/cuentas/*/pago", "/api/v1/cuentas/*/cerrar")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "CAJERO")

                        // Módulo Reservas
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas", "/api/v1/reservas/mesa/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservas/*")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "CAJERO", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/reservas/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "CLIENTE")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/reservas/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "CLIENTE")

                        // Módulo Eventos (MongoDB)
                        .requestMatchers(HttpMethod.GET, "/api/v1/eventos/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN", "MESERO", "COCINERO", "CHEF", "CAJERO")

                        // Módulo Usuarios (Administración)
                        .requestMatchers("/api/v1/usuarios/**")
                        .hasAnyRole("ADMINISTRADOR", "ADMIN")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )

                // 7. Soporte HTTP Basic para clientes compatibles
                .httpBasic(Customizer.withDefaults())

                // 8. Agregar filtro JWT antes de UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // 9. Configuración opcional OAuth2 si el registro está configurado
        if (clientRegistrationRepository != null) {
            http.oauth2Login(oauth2 -> oauth2
                    .successHandler(oAuth2SuccessHandler)
            );
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
