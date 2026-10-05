package com.restaurante.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.restaurante.mapper.EventoPedidoDocumentMapper;
import com.restaurante.model.document.EventoPedidoDocument;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.repository.IEventoPedidoRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventoPedidoServiceImplTest {

    @Mock
    private IEventoPedidoRepository repository;

    @Mock
    private EventoPedidoDocumentMapper documentMapper;

    @InjectMocks
    private EventoPedidoServiceImpl service;

    @Test
    @DisplayName("registrarCambioEstado - guarda el documento con el snapshot de items")
    void registrarCambioEstado_guardaEvento() {
        UUID idPedido = UUID.randomUUID();
        Pedido pedido = Pedido.builder().id(idPedido).idMesa(4L).estado(EstadoPedido.EN_PREPARACION)
                .items(List.of(ItemPedido.builder().idPlato(1L).nombrePlato("Hamburguesa")
                        .precioCongelado(20000.0).cantidad(2).build()))
                .build();

        when(documentMapper.toDocument(any(EventoPedido.class))).thenAnswer(inv -> {
            EventoPedido e = inv.getArgument(0);
            return EventoPedidoDocument.builder()
                    .idPedido(e.getIdPedido().toString())
                    .idMesa(e.getIdMesa())
                    .tipo(e.getTipo())
                    .estadoAnterior(e.getEstadoAnterior())
                    .estadoNuevo(e.getEstadoNuevo())
                    .timestamp(e.getTimestamp())
                    .build();
        });
        when(repository.save(any(EventoPedidoDocument.class))).thenAnswer(inv -> {
            EventoPedidoDocument d = inv.getArgument(0);
            d.setId("evt-1");
            return d;
        });
        when(documentMapper.toDomain(any(EventoPedidoDocument.class))).thenAnswer(inv -> {
            EventoPedidoDocument d = inv.getArgument(0);
            return EventoPedido.builder()
                    .id(d.getId())
                    .idPedido(UUID.fromString(d.getIdPedido()))
                    .idMesa(d.getIdMesa())
                    .tipo(d.getTipo())
                    .estadoAnterior(d.getEstadoAnterior())
                    .estadoNuevo(d.getEstadoNuevo())
                    .timestamp(d.getTimestamp())
                    .build();
        });

        EventoPedido resultado = service.registrarCambioEstado(
                pedido, EstadoPedido.RECIBIDO, EstadoPedido.EN_PREPARACION);

        assertEquals("evt-1", resultado.getId());
        assertEquals(idPedido, resultado.getIdPedido());
        assertEquals(EstadoPedido.RECIBIDO, resultado.getEstadoAnterior());
        assertEquals(EstadoPedido.EN_PREPARACION, resultado.getEstadoNuevo());
        verify(repository, times(1)).save(any(EventoPedidoDocument.class));
    }

    @Test
    @DisplayName("obtenerTodos - devuelve los eventos mapeados a dominio")
    void obtenerTodos_mapeaADominio() {
        EventoPedidoDocument doc = EventoPedidoDocument.builder()
                .id("evt-1").idPedido(UUID.randomUUID().toString()).idMesa(4L).tipo("CREACION").build();
        when(repository.findAllByOrderByTimestampDesc()).thenReturn(List.of(doc));
        when(documentMapper.toDomain(any(EventoPedidoDocument.class))).thenAnswer(inv -> {
            EventoPedidoDocument d = inv.getArgument(0);
            return EventoPedido.builder().id(d.getId()).idMesa(d.getIdMesa()).tipo(d.getTipo()).build();
        });

        List<EventoPedido> resultado = service.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("evt-1", resultado.get(0).getId());
    }
}
