package com.restaurante.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.entity.ItemPedidoEntity;

public interface IItemPedidoRepository extends JpaRepository<ItemPedidoEntity, Long> {

    List<ItemPedidoEntity> findByIdPlato(Long idPlato);

    /**
     * Indica si un plato está referenciado por algún ítem cuyo pedido NO esté en los
     * estados finales indicados. Se usa para impedir borrar un plato con pedidos activos.
     *
     * <p>La consulta se apoya en la asociación {@code ItemPedidoEntity.pedido} para llegar
     * al estado del pedido sin cargar la relación (la base de datos resuelve el JOIN).</p>
     */
    boolean existsByIdPlatoAndPedidoEstadoNotIn(Long idPlato, Collection<EstadoPedido> estadosFinales);
}
