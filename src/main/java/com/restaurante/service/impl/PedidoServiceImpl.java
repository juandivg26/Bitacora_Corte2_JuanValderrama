package com.restaurante.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurante.exception.RecursoNoEncontradoException;
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
import com.restaurante.service.IPedidoService;
import com.restaurante.service.IPlatoService;
import com.restaurante.util.UuidV7Generator;
import com.restaurante.validator.IPedidoValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {

    private final IPedidoRepository repository;
    private final PedidoEntityMapper entityMapper;
    private final IPlatoService platoService;
    private final IMesaService mesaService;
    private final IPedidoValidator validator;
    private final IEventoPedidoService eventoPedidoService;

    @Override
    public List<Pedido> obtenerTodos() {
        List<Pedido> pedidos = repository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
        log.info("Obteniendo todos los pedidos. Total: {}", pedidos.size());
        return pedidos;
    }

    @Override
    public List<Pedido> obtenerPorMesa(Long idMesa) {
        return repository.findByIdMesa(idMesa).stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    @Override
    public Pedido obtenerPorId(UUID id) {
        PedidoEntity entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pedido no encontrado: id={}", id);
                    return new RecursoNoEncontradoException("Pedido", id);
                });
        return entityMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Pedido crear(Pedido pedido) {
        Mesa mesa = mesaService.obtenerPorId(pedido.getIdMesa());
        validator.validarMesaDisponible(mesa);

        pedido.getItems().forEach(item -> congelarPrecio(item, item.getIdPlato()));

        pedido.setId(UuidV7Generator.generate());
        pedido.setTimestamp(LocalDateTime.now());
        pedido.setEstado(EstadoPedido.RECIBIDO);
        
        PedidoEntity guardado = repository.save(entityMapper.toEntity(pedido));
        Pedido resultado = entityMapper.toDomain(guardado);

        mesaService.cambiarEstado(mesa.getId(), EstadoMesa.OCUPADA);
        log.info("Pedido creado: id={}, idMesa={}, items={}",
                resultado.getId(), resultado.getIdMesa(), resultado.getItems().size());

        publicarEvento(() -> eventoPedidoService.registrarCreacion(resultado));
        return resultado;
    }

    @Override
    @Transactional
    public Pedido agregarItem(UUID idPedido, ItemPedido item) {
        Pedido pedido = obtenerPorId(idPedido);
        validator.validarPuedeModificarse(pedido);
        congelarPrecio(item, item.getIdPlato());
        pedido.agregarItem(item);
        
        PedidoEntity actualizado = repository.save(entityMapper.toEntity(pedido));
        Pedido resultado = entityMapper.toDomain(actualizado);
        log.info("Item agregado a pedido id={}: idPlato={}, cantidad={}",
                idPedido, item.getIdPlato(), item.getCantidad());
        return resultado;
    }

    @Override
    @Transactional
    public Pedido cambiarEstado(UUID id, EstadoPedido nuevoEstado) {
        Pedido pedido = obtenerPorId(id);
        validator.validarTransicionEstado(pedido, nuevoEstado);
        EstadoPedido estadoAnterior = pedido.getEstado();
        pedido.setEstado(nuevoEstado);

        PedidoEntity actualizado = repository.save(entityMapper.toEntity(pedido));
        Pedido resultado = entityMapper.toDomain(actualizado);
        log.info("Pedido id={} -> estado={}", id, nuevoEstado);

        publicarEvento(() -> eventoPedidoService.registrarCambioEstado(resultado, estadoAnterior, nuevoEstado));
        return resultado;
    }

    /**
     * El registro de eventos en MongoDB es no critico: si el motor NoSQL no esta
     * disponible, el flujo principal del pedido sigue funcionando igual.
     */
    private void publicarEvento(Runnable publicacion) {
        try {
            publicacion.run();
        } catch (Exception ex) {
            log.warn("No se pudo registrar el evento en MongoDB: {}", ex.getMessage());
        }
    }

    private void congelarPrecio(ItemPedido item, Long idPlato) {
        Plato plato = platoService.obtenerPorId(idPlato);
        validator.validarPlatoDisponible(plato);
        item.setNombrePlato(plato.getNombre());
        item.setPrecioCongelado(plato.getPrecio());
    }
}
