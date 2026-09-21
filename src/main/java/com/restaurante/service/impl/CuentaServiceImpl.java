package com.restaurante.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.restaurante.exception.RecursoNoEncontradoException;
import com.restaurante.model.domain.Cuenta;
import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.ItemPedido;
import com.restaurante.model.domain.Mesa;
import com.restaurante.model.domain.Pedido;
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

    private final Map<Long, Cuenta> cuentas = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    private final IMesaService mesaService;
    private final IPedidoService pedidoService;
    private final ICuentaValidator validator;

    @Override
    public List<Cuenta> obtenerTodas() {
        log.info("Obteniendo todas las cuentas. Total: {}", cuentas.size());
        return cuentas.values().stream().toList();
    }

    @Override
    public Cuenta obtenerPorId(Long id) {
        return cuentas.values().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Cuenta no encontrada: id={}", id);
                    return new RecursoNoEncontradoException("Cuenta", id);
                });
    }

    @Override
    public Cuenta obtenerPorMesa(Long idMesa) {
        return cuentas.values().stream()
                .filter(c -> c.getIdMesa().equals(idMesa))
                .filter(c -> c.getEstado() != EstadoCuenta.CERRADA)
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta abierta para mesa", idMesa));
    }

    @Override
    public Cuenta abrir(Long idMesa) {
        Mesa mesa = mesaService.obtenerPorId(idMesa);
        validator.validarMesaSinCuentaAbierta(mesa);

        Cuenta cuenta = Cuenta.builder()
                .id(contador.getAndIncrement())
                .idMesa(idMesa)
                .total(0.0)
                .estado(EstadoCuenta.ABIERTA)
                .fechaApertura(LocalDateTime.now())
                .build();
        cuentas.put(cuenta.getId(), cuenta);
        mesa.abrirCuenta();
        log.info("Cuenta abierta: id={}, idMesa={}", cuenta.getId(), idMesa);
        return cuenta;
    }

    @Override
    public Cuenta registrarPago(Long id) {
        Cuenta cuenta = obtenerPorId(id);
        validator.validarTransicionEstado(cuenta, EstadoCuenta.EN_PAGO);

        List<ItemPedido> items = pedidoService.obtenerPorMesa(cuenta.getIdMesa()).stream()
                .flatMap(pedido -> pedido.getItems().stream())
                .toList();
        cuenta.calcularTotal(items);
        cuenta.registrarPago();
        log.info("Pago registrado: id={}, total={}", id, cuenta.getTotal());
        return cuenta;
    }

    @Override
    public Cuenta cerrar(Long id) {
        Cuenta cuenta = obtenerPorId(id);
        validator.validarTransicionEstado(cuenta, EstadoCuenta.CERRADA);
        cuenta.cerrarCuenta();
        Mesa mesa = mesaService.obtenerPorId(cuenta.getIdMesa());
        mesa.cerrarCuenta();
        log.info("Cuenta cerrada: id={}", id);
        return cuenta;
    }
}
