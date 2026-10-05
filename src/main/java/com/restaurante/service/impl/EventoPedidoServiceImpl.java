package com.restaurante.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.restaurante.mapper.EventoPedidoDocumentMapper;
import com.restaurante.model.document.EventoPedidoDocument;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Pedido;
import com.restaurante.repository.IEventoPedidoRepository;
import com.restaurante.service.IEventoPedidoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Persistencia NoSQL: registra en MongoDB el historial de eventos de los pedidos.
 * El dominio no sabe que el almacenamiento es Mongo; el DocumentMapper traduce.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EventoPedidoServiceImpl implements IEventoPedidoService {

    private static final String TIPO_CREACION = "CREACION";
    private static final String TIPO_CAMBIO_ESTADO = "CAMBIO_ESTADO";

    private final IEventoPedidoRepository repository;
    private final EventoPedidoDocumentMapper documentMapper;

    @Override
    public EventoPedido registrarCreacion(Pedido pedido) {
        return registrar(pedido, null, pedido.getEstado(), TIPO_CREACION);
    }

    @Override
    public EventoPedido registrarCambioEstado(Pedido pedido, EstadoPedido estadoAnterior, EstadoPedido estadoNuevo) {
        return registrar(pedido, estadoAnterior, estadoNuevo, TIPO_CAMBIO_ESTADO);
    }

    @Override
    public List<EventoPedido> obtenerTodos() {
        List<EventoPedido> eventos = repository.findAllByOrderByTimestampDesc().stream()
                .map(documentMapper::toDomain)
                .toList();
        log.info("Obteniendo eventos de pedido. Total: {}", eventos.size());
        return eventos;
    }

    @Override
    public List<EventoPedido> obtenerPorPedido(UUID idPedido) {
        return repository.findByIdPedidoOrderByTimestampDesc(idPedido.toString()).stream()
                .map(documentMapper::toDomain)
                .toList();
    }

    private EventoPedido registrar(Pedido pedido, EstadoPedido anterior, EstadoPedido nuevo, String tipo) {
        List<ItemPedido> snapshot = pedido.getItems() == null ? List.of() : List.copyOf(pedido.getItems());

        EventoPedido evento = EventoPedido.builder()
                .idPedido(pedido.getId())
                .idMesa(pedido.getIdMesa())
                .tipo(tipo)
                .estadoAnterior(anterior)
                .estadoNuevo(nuevo)
                .timestamp(LocalDateTime.now())
                .items(snapshot)
                .build();

        EventoPedidoDocument guardado = repository.save(documentMapper.toDocument(evento));
        EventoPedido resultado = documentMapper.toDomain(guardado);
        log.info("Evento registrado en MongoDB: pedido={}, {} -> {} ({})",
                resultado.getIdPedido(), anterior, nuevo, tipo);
        return resultado;
    }
}
