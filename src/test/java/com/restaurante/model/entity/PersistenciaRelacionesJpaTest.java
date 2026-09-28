package com.restaurante.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.restaurante.model.domain.EstadoCuenta;
import com.restaurante.model.domain.EstadoMesa;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.repository.ICuentaRepository;
import com.restaurante.repository.IMesaRepository;
import com.restaurante.repository.IPedidoRepository;
import com.restaurante.repository.IReservaRepository;

import jakarta.persistence.EntityManager;

/**
 * Prueba de persistencia real (con base de datos embebida) que verifica que el
 * mapeo de las relaciones @ManyToOne hacia Mesa se construye correctamente y que
 * la FK se puede leer por el campo de solo lectura sin inicializar la relación.
 */
@DataJpaTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class PersistenciaRelacionesJpaTest {

    @Autowired
    private IMesaRepository mesaRepository;

    @Autowired
    private IPedidoRepository pedidoRepository;

    @Autowired
    private ICuentaRepository cuentaRepository;

    @Autowired
    private IReservaRepository reservaRepository;

    @Autowired
    private EntityManager em;

    private MesaEntity mesaGuardada() {
        return mesaRepository.saveAndFlush(MesaEntity.builder()
                .numero(1).capacidad(4).estado(EstadoMesa.DISPONIBLE).cuentaAbierta(false).build());
    }

    @Test
    @DisplayName("Pedido: guarda la relación con la mesa, consulta por mesaId y no inicializa el proxy LAZY")
    void pedido_relacionConMesa_funciona() {
        MesaEntity mesa = mesaGuardada();

        PedidoEntity pedido = PedidoEntity.builder()
                .id(UUID.randomUUID())
                .mesa(mesa)
                .idMesa(mesa.getId())
                .estado(EstadoPedido.RECIBIDO)
                .timestamp(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        ItemPedidoEntity item = ItemPedidoEntity.builder()
                .idPlato(1L).nombrePlato("Hamburguesa").precioCongelado(20000.0).cantidad(2).build();
        item.setPedido(pedido);
        pedido.getItems().add(item);

        pedidoRepository.saveAndFlush(pedido);
        em.clear();

        PedidoEntity recargado = pedidoRepository.findById(pedido.getId()).orElseThrow();

        assertEquals(mesa.getId(), recargado.getIdMesa(), "El campo idMesa (solo lectura) debe poblarse");
        assertEquals(mesa.getId(), recargado.getMesa().getId(), "La relación debe apuntar a la mesa");
        assertFalse(Hibernate.isInitialized(recargado.getMesa()),
                "Leer idMesa no debe inicializar la relación LAZY");
        assertEquals(1, pedidoRepository.findByIdMesa(mesa.getId()).size());
        assertEquals(1, recargado.getItems().size());
    }

    @Test
    @DisplayName("Cuenta y Reserva: guardan y consultan su relación con la mesa")
    void cuentaYReserva_relacionConMesa_funciona() {
        MesaEntity mesa = mesaGuardada();

        cuentaRepository.saveAndFlush(CuentaEntity.builder()
                .mesa(mesa).idMesa(mesa.getId()).total(0.0)
                .estado(EstadoCuenta.ABIERTA).fechaApertura(LocalDateTime.now()).build());
        reservaRepository.saveAndFlush(ReservaEntity.builder()
                .id(UUID.randomUUID()).mesa(mesa).idMesa(mesa.getId()).cliente("Juan Pérez")
                .fechaHora(LocalDateTime.now().plusDays(1)).comensales(4).cancelada(false).build());
        em.clear();

        assertTrue(cuentaRepository.findByIdMesa(mesa.getId()).stream()
                .allMatch(c -> mesa.getId().equals(c.getIdMesa())));
        assertTrue(reservaRepository.findByIdMesa(mesa.getId()).stream()
                .allMatch(r -> mesa.getId().equals(r.getIdMesa())));
    }
}
