package com.restaurante.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.model.domain.Rol;
import com.restaurante.model.dto.request.LoginRequestDTO;
import com.restaurante.model.dto.request.PlatoRequestDTO;
import com.restaurante.model.dto.request.RegistroUsuarioRequestDTO;
import com.restaurante.security.jwt.JwtUtil;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:securitydb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "logging.level.org.mongodb.driver.cluster=OFF"
})
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Login exitoso devuelve status 200 y token JWT válido")
    void login_exitoso_devuelveTokenJwt() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("admin@americanbites.com", "Admin123*");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("admin@americanbites.com"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"));
    }

    @Test
    @DisplayName("Login con credenciales incorrectas devuelve status 401")
    void login_credencialesIncorrectas_devuelve401() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("admin@americanbites.com", "PasswordErroneo");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Registro de nuevo usuario devuelve status 201")
    void registro_nuevoUsuario_devuelve201() throws Exception {
        RegistroUsuarioRequestDTO request = RegistroUsuarioRequestDTO.builder()
                .email("nuevo_cliente@americanbites.com")
                .password("PasswordSeguro123")
                .nombre("Nuevo Cliente")
                .rol(Rol.CLIENTE)
                .build();

        mockMvc.perform(post("/api/v1/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("nuevo_cliente@americanbites.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    @DisplayName("Endpoint público /menu devuelve status 200 sin token")
    void verMenu_sinAutenticacion_devuelve200() throws Exception {
        mockMvc.perform(get("/api/v1/menu"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Crear plato sin autenticación devuelve status 401")
    void crearPlato_sinToken_devuelve401() throws Exception {
        PlatoRequestDTO dto = new PlatoRequestDTO("Hamburguesa Doble", 25000.0, "HAMBURGUESAS", "Doble carne con queso");

        mockMvc.perform(post("/api/v1/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Crear plato con token inválido devuelve status 401")
    void crearPlato_conTokenInvalido_devuelve401() throws Exception {
        PlatoRequestDTO dto = new PlatoRequestDTO("Hamburguesa Doble", 25000.0, "HAMBURGUESAS", "Doble carne con queso");

        mockMvc.perform(post("/api/v1/platos")
                        .header("Authorization", "Bearer token_falso_invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Crear plato con rol CLIENTE devuelve status 403 Forbidden")
    @WithMockUser(username = "cliente@americanbites.com", roles = {"CLIENTE"})
    void crearPlato_conRolCliente_devuelve403() throws Exception {
        PlatoRequestDTO dto = new PlatoRequestDTO("Hamburguesa Doble", 25000.0, "HAMBURGUESAS", "Doble carne con queso");

        mockMvc.perform(post("/api/v1/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Crear plato con rol ADMINISTRADOR devuelve status 201 Created")
    @WithMockUser(username = "admin@americanbites.com", roles = {"ADMINISTRADOR", "ADMIN"})
    void crearPlato_conRolAdmin_devuelve201() throws Exception {
        PlatoRequestDTO dto = new PlatoRequestDTO("Super Perro Caliente", 18000.0, "PERROS", "Perro con tocineta");

        mockMvc.perform(post("/api/v1/platos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Super Perro Caliente"));
    }

    @Test
    @DisplayName("Crear plato autenticando con HTTP Basic (Admin) devuelve status 201")
    void crearPlato_conHttpBasicAdmin_devuelve201() throws Exception {
        PlatoRequestDTO dto = new PlatoRequestDTO("Salchipapa Especial", 22000.0, "ENTRADAS", "Papas con salchicha");

        mockMvc.perform(post("/api/v1/platos")
                        .with(httpBasic("admin@americanbites.com", "Admin123*"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Consultar /me con token JWT válido devuelve los datos del usuario")
    void me_conTokenJwt_devuelveDatosUsuario() throws Exception {
        String token = jwtUtil.generateToken("mesero@americanbites.com", "MESERO");

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mesero@americanbites.com"))
                .andExpect(jsonPath("$.rol").value("MESERO"));
    }

    @Test
    @DisplayName("Consultar usuarios con rol CLIENTE devuelve status 403 Forbidden")
    @WithMockUser(username = "cliente@americanbites.com", roles = {"CLIENTE"})
    void consultarUsuarios_conRolCliente_devuelve403() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Consultar usuarios con rol ADMINISTRADOR devuelve status 200 OK")
    @WithMockUser(username = "admin@americanbites.com", roles = {"ADMINISTRADOR", "ADMIN"})
    void consultarUsuarios_conRolAdmin_devuelve200() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Verificar presencia de cabeceras HTTP de seguridad (X-Frame-Options, XSS, CSP)")
    void headersSeguridad_presentesEnRespuesta() throws Exception {
        mockMvc.perform(get("/api/v1/menu"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().exists("Content-Security-Policy"));
    }
}
