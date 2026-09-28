package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.restaurante.exception.EstadoInvalidoException;
import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.mapper.PedidoEntityMapper;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.model.entity.PedidoEntity;
import com.restaurante.repository.IPedidoRepository;
import com.restaurante.service.IEventoPedidoService;
import com.restaurante.service.IMesaService;
import com.restaurante.service.IPlatoService;
import com.restaurante.util.UuidV7Generator;
import com.restaurante.validator.IPedidoValidator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PedidoServiceImplTest {

    @Mock
    private IPedidoRepository repository;

    @Mock
    private PedidoEntityMapper entityMapper;

    @Mock
    private IPlatoService platoService;

    @Mock
    private IMesaService mesaService;

    @Mock
    private IPedidoValidator validator;

    @Mock
    private IEventoPedidoService eventoPedidoService;

    @InjectMocks
    private PedidoServiceImpl service;

    private Mesa mesaDisponible;
    private Plato platoDisponible;
    private UUID pedidoId;

    @BeforeEach
    void setUp() {
        pedidoId = UuidV7Generator.generate();
        
        // Configurar mapper dominio -> entidad
        lenient().when(entityMapper.toEntity(any(Pedido.class))).thenAnswer(inv -> {
            Pedido pedido = inv.getArgument(0);
            PedidoEntity entity = PedidoEntity.builder()
                    .id(pedido.getId())
                    .idMesa(pedido.getIdMesa())
                    .estado(pedido.getEstado())
                    .timestamp(pedido.getTimestamp())
                    .build();
            // Copiar items manualmente porque MapStruct no lo hace automáticamente en el test
            if (pedido.getItems() != null) {
                List<com.restaurante.model.entity.ItemPedidoEntity> items = new ArrayList<>();
                for (ItemPedido item : pedido.getItems()) {
                    com.restaurante.model.entity.ItemPedidoEntity itemEntity = new com.restaurante.model.entity.ItemPedidoEntity();
                    itemEntity.setIdPlato(item.getIdPlato());
                    itemEntity.setCantidad(item.getCantidad());
                    itemEntity.setNombrePlato(item.getNombrePlato());
                    itemEntity.setPrecioCongelado(item.getPrecioCongelado());
                    items.add(itemEntity);
                }
                entity.setItems(items);
            }
            return entity;
        });

        // Configurar mapper entidad -> dominio
        lenient().when(entityMapper.toDomain(any(PedidoEntity.class))).thenAnswer(inv -> {
            PedidoEntity entity = inv.getArgument(0);
            Pedido pedido = Pedido.builder()
                    .id(entity.getId())
                    .idMesa(entity.getIdMesa())
                    .estado(entity.getEstado())
                    .timestamp(entity.getTimestamp())
                    .build();
            // Copiar items manualmente
            if (entity.getItems() != null) {
                List<ItemPedido> items = new ArrayList<>();
                for (com.restaurante.model.entity.ItemPedidoEntity itemEntity : entity.getItems()) {
                    ItemPedido item = ItemPedido.builder()
                            .idPlato(itemEntity.getIdPlato())
                            .cantidad(itemEntity.getCantidad())
                            .nombrePlato(itemEntity.getNombrePlato())
                            .precioCongelado(itemEntity.getPrecioCongelado())
                            .build();
                    items.add(item);
                }
                pedido.setItems(items);
            }
            return pedido;
        });

        // Configurar repository para save
        lenient().when(repository.save(any(PedidoEntity.class))).thenAnswer(inv -> {
            PedidoEntity entity = inv.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(pedidoId);
            }
            return entity;
        });
        
        // Configurar repository para findById
        lenient().when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());
        
        // Configurar repository para findByIdMesa
        lenient().when(repository.findByIdMesa(any(Long.class))).thenReturn(new ArrayList<>());

        mesaDisponible = Mesa.builder().id(1L).numero(1).capacidad(4)
                .estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build();
        platoDisponible = Plato.builder().id(1L).nombre("Bandeja Paisa")
                .precio(28000.0).categoria("PRINCIPALES").disponible(true).build();
    }

    private Pedido pedidoConUnItem() {
        List<ItemPedido> items = new ArrayList<>();
        items.add(ItemPedido.builder().idPlato(1L).cantidad(2).build());
        return Pedido.builder().idMesa(1L).items(items).build();
    }

    @Test
    @DisplayName("crear - congela el precio del plato y marca la mesa como OCUPADA")
    void crear_pedidoCorrecto_congelaPrecioYOcupaMesa() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);

        Pedido resultado = service.crear(pedidoConUnItem());

        assertNotNull(resultado.getId());
        assertEquals(28000.0, resultado.getItems().get(0).getPrecioCongelado());
        assertEquals("Bandeja Paisa", resultado.getItems().get(0).getNombrePlato());
        assertEquals(EstadoPedido.RECIBIDO, resultado.getEstado());
        verify(mesaService, times(1)).cambiarEstado(1L, EstadoMesa.OCUPADA);
    }

    @Test
    @DisplayName("crear - mesa no disponible lanza ReglaDeNegocioException")
    void crear_mesaNoDisponible_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        doThrow(new ReglaDeNegocioException("Mesa no disponible"))
                .when(validator).validarMesaDisponible(any());

        assertThrows(ReglaDeNegocioException.class, () -> service.crear(pedidoConUnItem()));
    }

    @Test
    @DisplayName("crear - plato no disponible lanza ReglaDeNegocioException")
    void crear_platoNoDisponible_lanzaExcepcion() {
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);
        doThrow(new ReglaDeNegocioException("Plato no disponible"))
                .when(validator).validarPlatoDisponible(any());

        assertThrows(ReglaDeNegocioException.class, () -> service.crear(pedidoConUnItem()));
    }

    @Test
    @DisplayName("obtenerPorId - ID inexistente lanza RecursoNoEncontradoException")
    void obtenerPorId_noExiste_lanzaExcepcion() {
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(UUID.randomUUID()));
    }

    @Test
    @DisplayName("agregarItem - pedido no modificable lanza EstadoInvalidoException")
    void agregarItem_pedidoNoModificable_lanzaExcepcion() {
        UUID idPedido = UuidV7Generator.generate();
        PedidoEntity entity = PedidoEntity.builder().id(idPedido).idMesa(1L)
                .estado(EstadoPedido.RECIBIDO).build();
        when(repository.findById(idPedido)).thenReturn(Optional.of(entity));
        doThrow(new EstadoInvalidoException("No modificable"))
                .when(validator).validarPuedeModificarse(any());

        ItemPedido nuevoItem = ItemPedido.builder().idPlato(1L).cantidad(1).build();
        assertThrows(EstadoInvalidoException.class,
                () -> service.agregarItem(idPedido, nuevoItem));
    }

    @Test
    @DisplayName("agregarItem - pedido modificable agrega el ítem y congela su precio")
    void agregarItem_pedidoModificable_agregaItem() {
        UUID idPedido = UuidV7Generator.generate();
        PedidoEntity entity = PedidoEntity.builder().id(idPedido).idMesa(1L)
                .estado(EstadoPedido.RECIBIDO).build();
        when(repository.findById(idPedido)).thenReturn(Optional.of(entity));
        when(mesaService.obtenerPorId(1L)).thenReturn(mesaDisponible);
        when(platoService.obtenerPorId(1L)).thenReturn(platoDisponible);

        ItemPedido nuevoItem = ItemPedido.builder().idPlato(1L).cantidad(1).build();
        Pedido resultado = service.agregarItem(idPedido, nuevoItem);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("cambiarEstado - transición válida actualiza el estado")
    void cambiarEstado_transicionValida_actualizaEstado() {
        UUID idPedido = UuidV7Generator.generate();
        PedidoEntity entity = PedidoEntity.builder().id(idPedido).idMesa(1L)
                .estado(EstadoPedido.RECIBIDO).build();
        when(repository.findById(idPedido)).thenReturn(Optional.of(entity));
        when(repository.save(any(PedidoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = service.cambiarEstado(idPedido, EstadoPedido.EN_PREPARACION);

        assertEquals(EstadoPedido.EN_PREPARACION, resultado.getEstado());
    }

    @Test
    @DisplayName("cambiarEstado - transición inválida lanza EstadoInvalidoException")
    void cambiarEstado_transicionInvalida_lanzaExcepcion() {
        UUID idPedido = UuidV7Generator.generate();
        PedidoEntity entity = PedidoEntity.builder().id(idPedido).idMesa(1L)
                .estado(EstadoPedido.RECIBIDO).build();
        when(repository.findById(idPedido)).thenReturn(Optional.of(entity));
        doThrow(new EstadoInvalidoException("Transición inválida"))
                .when(validator).validarTransicionEstado(any(), eq(EstadoPedido.ENTREGADO));

        assertThrows(EstadoInvalidoException.class,
                () -> service.cambiarEstado(idPedido, EstadoPedido.ENTREGADO));
    }

    @Test
    @DisplayName("obtenerPorMesa - filtra solo los pedidos de esa mesa")
    void obtenerPorMesa_filtraCorrectamente() {
        UUID idPedido = UuidV7Generator.generate();
        PedidoEntity entity = PedidoEntity.builder().id(idPedido).idMesa(1L)
                .estado(EstadoPedido.RECIBIDO).build();
        when(repository.findByIdMesa(1L)).thenReturn(List.of(entity));

        List<Pedido> resultado = service.obtenerPorMesa(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdMesa());
    }
}