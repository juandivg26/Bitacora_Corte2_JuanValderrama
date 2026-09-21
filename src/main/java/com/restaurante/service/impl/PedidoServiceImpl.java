package com.restaurante.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
import com.restaurante.model.domain.Plato;
import com.restaurante.service.IMesaService;
import com.restaurante.service.IPedidoService;
import com.restaurante.service.IPlatoService;
import com.restaurante.validator.IPedidoValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {

    private final Map<Long, Pedido> pedidos = new ConcurrentHashMap<>();
    private final AtomicLong contadorPedidos = new AtomicLong(1);
    private final AtomicLong contadorItems = new AtomicLong(1);

    private final IPlatoService platoService;
    private final IMesaService mesaService;
    private final IPedidoValidator validator;

    @Override
    public List<Pedido> obtenerTodos() {
        log.info("Obteniendo todos los pedidos. Total: {}", pedidos.size());
        return pedidos.values().stream().toList();
    }

    @Override
    public List<Pedido> obtenerPorMesa(Long idMesa) {
        return pedidos.values().stream()
                .filter(p -> p.getIdMesa().equals(idMesa))
                .toList();
    }

    @Override
    public Pedido obtenerPorId(Long id) {
        return pedidos.values().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado: id={}", id);
                    return new RecursoNoEncontradoException("Pedido", id);
                });
    }

    @Override
    public Pedido crear(Pedido pedido) {
        Mesa mesa = mesaService.obtenerPorId(pedido.getIdMesa());
        validator.validarMesaDisponible(mesa);

        pedido.getItems().forEach(item -> congelarPrecio(item, item.getIdPlato()));

        pedido.setId(contadorPedidos.getAndIncrement());
        pedido.setTimestamp(LocalDateTime.now());
        pedidos.put(pedido.getId(), pedido);

        mesaService.cambiarEstado(mesa.getId(), EstadoMesa.OCUPADA);
        log.info("Pedido creado: id={}, idMesa={}, items={}",
                pedido.getId(), pedido.getIdMesa(), pedido.getItems().size());
        return pedido;
    }

    @Override
    public Pedido agregarItem(Long idPedido, ItemPedido item) {
        Pedido pedido = obtenerPorId(idPedido);
        validator.validarPuedeModificarse(pedido);
        congelarPrecio(item, item.getIdPlato());
        pedido.agregarItem(item);
        log.info("Item agregado a pedido id={}: idPlato={}, cantidad={}",
                idPedido, item.getIdPlato(), item.getCantidad());
        return pedido;
    }

    @Override
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPorId(id);
        validator.validarTransicionEstado(pedido, nuevoEstado);
        pedido.setEstado(nuevoEstado);
        log.info("Pedido id={} -> estado={}", id, nuevoEstado);
        return pedido;
    }

    private void congelarPrecio(ItemPedido item, Long idPlato) {
        Plato plato = platoService.obtenerPorId(idPlato);
        validator.validarPlatoDisponible(plato);
        item.setId(contadorItems.getAndIncrement());
        item.setNombrePlato(plato.getNombre());
        item.setPrecioCongelado(plato.getPrecio());
    }
}
