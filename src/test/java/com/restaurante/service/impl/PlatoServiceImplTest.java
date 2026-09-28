package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.PlatoEntityMapper;
import com.restaurante.mapper.PlatoEntityMapperImpl;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.entity.PlatoEntity;
import com.restaurante.repository.IPlatoRepository;
import com.restaurante.validator.IPlatoValidator;

@ExtendWith(MockitoExtension.class)
class PlatoServiceImplTest {

    @Mock
    private IPlatoRepository repository;

    @Mock
    private IPlatoValidator validator;

    @InjectMocks
    private PlatoServiceImpl service;

    private final PlatoEntityMapper entityMapper = new PlatoEntityMapperImpl();
    private final Map<Long, PlatoEntity> almacen = new HashMap<>();

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(service, "entityMapper", entityMapper);
        almacen.clear();
        lenient().when(repository.save(any(PlatoEntity.class))).thenAnswer(inv -> {
            PlatoEntity plato = inv.getArgument(0);
            if (plato.getId() == null) {
                plato.setId((long) (almacen.size() + 1));
            }
            almacen.put(plato.getId(), plato);
            return plato;
        });
        lenient().when(repository.findById(anyLong()))
                .thenAnswer(inv -> Optional.ofNullable(almacen.get(inv.getArgument(0))));
        lenient().when(repository.findAll()).thenAnswer(inv -> new ArrayList<>(almacen.values()));
        lenient().doAnswer(inv -> almacen.remove(inv.getArgument(0)))
                .when(repository).deleteById(anyLong());
    }

    private PlatoEntity platoEntity(String nombre, double precio, String categoria, boolean disponible) {
        return PlatoEntity.builder()
                .id(null)
                .nombre(nombre)
                .precio(precio)
                .categoria(categoria)
                .disponible(disponible)
                .build();
    }

    @Test
    @DisplayName("crear - guarda el plato y le asigna un ID")
    void crear_platoCorrecto_guardaYRetorna() {
        Plato entrada = Plato.builder()
                .nombre("Bandeja Paisa").precio(28000.0)
                .categoria("PRINCIPALES").disponible(true).build();

        Plato resultado = service.crear(entrada);

        assertNotNull(resultado.getId());
        assertEquals("Bandeja Paisa", resultado.getNombre());
        verify(validator, times(1)).validarNombreUnico("Bandeja Paisa");
    }

    @Test
    @DisplayName("crear - nombre duplicado lanza ConflictoException")
    void crear_nombreDuplicado_lanzaConflicto() {
        org.mockito.Mockito.doThrow(new ConflictoException("Nombre duplicado"))
                .when(validator).validarNombreUnico("Bandeja Paisa");

        Plato plato = Plato.builder().nombre("Bandeja Paisa")
                .precio(28000.0).categoria("PRINCIPALES").build();

        assertThrows(ConflictoException.class, () -> service.crear(plato));
    }

    @Test
    @DisplayName("obtenerPorId - ID inexistente lanza RecursoNoEncontradoException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    @DisplayName("obtenerTodos - sin platos devuelve lista vacía")
    void obtenerTodos_sinPlatos_devuelveListaVacia() {
        List<Plato> resultado = service.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("obtenerDisponibles - filtra solo los disponibles")
    void obtenerDisponibles_filtraSoloDisponibles() {
        almacen.put(1L, platoEntity("A", 10.0, "X", true));
        almacen.put(2L, platoEntity("B", 10.0, "X", false));

        List<Plato> resultado = service.obtenerDisponibles();

        assertEquals(1, resultado.size());
        assertEquals("A", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("obtenerPorCategoria - filtra ignorando mayúsculas/minúsculas")
    void obtenerPorCategoria_filtraCorrectamente() {
        almacen.put(1L, platoEntity("Ajiaco", 20000.0, "SOPAS", true));
        almacen.put(2L, platoEntity("Bandeja", 28000.0, "PRINCIPALES", true));

        List<Plato> resultado = service.obtenerPorCategoria("sopas");

        assertEquals(1, resultado.size());
        assertEquals("Ajiaco", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("cambiarDisponibilidad - desactiva un plato existente")
    void cambiarDisponibilidad_desactiva_correctamente() {
        PlatoEntity entity = platoEntity("Sopa", 15000.0, "ENTRADAS", true);
        entity.setId(1L);
        almacen.put(1L, entity);

        Plato resultado = service.cambiarDisponibilidad(1L, false);

        assertFalse(resultado.estaDisponible());
    }

    @Test
    @DisplayName("actualizar - modifica los datos de un plato existente")
    void actualizar_platoExistente_modificaDatos() {
        PlatoEntity entity = platoEntity("Sopa", 15000.0, "ENTRADAS", true);
        entity.setId(1L);
        almacen.put(1L, entity);

        Plato nuevosDatos = Plato.builder().nombre("Sopa Especial")
                .precio(17000.0).categoria("PRINCIPALES").build();

        Plato resultado = service.actualizar(1L, nuevosDatos);

        assertEquals("Sopa Especial", resultado.getNombre());
        assertEquals(17000.0, resultado.getPrecio());
        verify(validator).validarNombreUnico("Sopa Especial");
    }

    @Test
    @DisplayName("eliminar - borra el plato existente")
    void eliminar_platoExistente_loElimina() {
        PlatoEntity entity = platoEntity("Sopa", 15000.0, "ENTRADAS", true);
        entity.setId(1L);
        almacen.put(1L, entity);

        service.eliminar(1L);

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(1L));
    }
}
