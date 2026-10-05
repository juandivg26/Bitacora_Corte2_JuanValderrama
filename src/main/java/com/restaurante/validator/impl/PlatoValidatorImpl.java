package com.restaurante.validator.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.restaurante.exception.ConflictoException;
import com.restaurante.exception.ReglaDeNegocioException;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.repository.IItemPedidoRepository;
import com.restaurante.repository.IPlatoRepository;
import com.restaurante.util.TextoUtils;
import com.restaurante.validator.IPlatoValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PlatoValidatorImpl implements IPlatoValidator {

    /**
     * Estados en los que un pedido ya no cambia. La lista se deriva del enum
     * ({@link EstadoPedido#esFinal()}), no se escribe a mano, para no duplicar la regla.
     */
    private static final List<EstadoPedido> ESTADOS_FINALES = Arrays.stream(EstadoPedido.values())
            .filter(EstadoPedido::esFinal)
            .toList();

    private final IPlatoRepository platoRepository;
    private final IItemPedidoRepository itemPedidoRepository;

    @Override
    public void validarSinPedidosActivos(Long idPlato) {
        if (itemPedidoRepository.existsByIdPlatoAndPedidoEstadoNotIn(idPlato, ESTADOS_FINALES)) {
            log.warn("No se puede eliminar el plato id={}: tiene pedidos activos", idPlato);
            throw new ConflictoException(
                    "No se puede eliminar el plato id=" + idPlato + " porque tiene pedidos activos");
        }
    }

    @Override
    public void validarNombreUnico(String nombre) {
        if (TextoUtils.esVacio(nombre)) {
            log.warn("Nombre de plato vacio o nulo");
            throw new ReglaDeNegocioException("El nombre del plato no puede estar vacio");
        }
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            log.warn("Nombre de plato duplicado: {}", nombre);
            throw new ConflictoException("Ya existe un plato con el nombre '" + nombre + "'");
        }
    }
}
