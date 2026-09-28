package com.restaurante.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.mapper.CuentaEntityMapper;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.entity.CuentaEntity;
import com.restaurante.repository.ICuentaRepository;
import com.restaurante.service.ICuentaService;
import com.restaurante.service.IMesaService;
import com.restaurante.service.IPedidoService;
import com.restaurante.validator.ICuentaValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CuentaServiceImpl implements ICuentaService {

    private final ICuentaRepository repository;
    private final CuentaEntityMapper entityMapper;
    private final IMesaService mesaService;
    private final IPedidoService pedidoService;
    private final ICuentaValidator validator;

    @Override
    public List<Cuenta> obtenerTodas() {
        List<Cuenta> cuentas = repository.findAll().stream()
                .map(entityMapper::toDomain)
                .toList();
        log.info("Obteniendo todas las cuentas. Total: {}", cuentas.size());
        return cuentas;
    }

    @Override
    public Cuenta obtenerPorId(Long id) {
        CuentaEntity entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cuenta no encontrada: id={}", id);
                    return new RecursoNoEncontradoException("Cuenta", id);
                });
        return entityMapper.toDomain(entity);
    }

    @Override
    public Cuenta obtenerPorMesa(Long idMesa) {
        List<CuentaEntity> cuentas = repository.findByIdMesa(idMesa);
        CuentaEntity cuentaAbierta = cuentas.stream()
                .filter(c -> c.getEstado() != EstadoCuenta.CERRADA)
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta abierta para mesa", idMesa));
        return entityMapper.toDomain(cuentaAbierta);
    }

    @Override
    public Cuenta abrir(Long idMesa) {
        Mesa mesa = mesaService.obtenerPorId(idMesa);
        validator.validarMesaSinCuentaAbierta(mesa);

        Cuenta cuenta = Cuenta.builder()
                .idMesa(idMesa)
                .total(0.0)
                .estado(EstadoCuenta.ABIERTA)
                .fechaApertura(LocalDateTime.now())
                .build();
        
        CuentaEntity guardado = repository.save(entityMapper.toEntity(cuenta));
        Cuenta resultado = entityMapper.toDomain(guardado);

        // Persistir en la mesa: estado OCUPADA y cuentaAbierta = true
        mesaService.abrirCuenta(idMesa);
        log.info("Cuenta abierta: id={}, idMesa={}", resultado.getId(), idMesa);
        return resultado;
    }

    @Override
    public Cuenta registrarPago(Long id) {
        Cuenta cuenta = obtenerPorId(id);
        validator.validarTransicionEstado(cuenta, EstadoCuenta.EN_PAGO);

        List<ItemPedido> items = pedidoService.obtenerPorMesa(cuenta.getIdMesa()).stream()
                .filter(pedido -> pedido.getEstado() != EstadoPedido.CANCELADO)
                .flatMap(pedido -> pedido.getItems().stream())
                .toList();
        cuenta.calcularTotal(items);
        cuenta.registrarPago();
        
        CuentaEntity actualizado = repository.save(entityMapper.toEntity(cuenta));
        Cuenta resultado = entityMapper.toDomain(actualizado);
        log.info("Pago registrado: id={}, total={}", id, resultado.getTotal());
        return resultado;
    }

    @Override
    public Cuenta cerrar(Long id) {
        Cuenta cuenta = obtenerPorId(id);
        validator.validarTransicionEstado(cuenta, EstadoCuenta.CERRADA);
        cuenta.cerrarCuenta();
        
        CuentaEntity actualizado = repository.save(entityMapper.toEntity(cuenta));
        Cuenta resultado = entityMapper.toDomain(actualizado);

        // Persistir en la mesa: estado DISPONIBLE y cuentaAbierta = false
        mesaService.cerrarCuenta(cuenta.getIdMesa());
        log.info("Cuenta cerrada: id={}", id);
        return resultado;
    }
}
