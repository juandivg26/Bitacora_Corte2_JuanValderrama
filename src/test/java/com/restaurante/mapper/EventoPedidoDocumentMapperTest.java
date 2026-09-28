package com.restaurante.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.restaurante.model.document.EventoPedidoDocument;
import com.restaurante.model.domain.EstadoPedido;
import com.restaurante.model.domain.EventoPedido;
import com.restaurante.model.domain.ItemPedido;

class EventoPedidoDocumentMapperTest {

    private final EventoPedidoDocumentMapper mapper = new EventoPedidoDocumentMapperImpl();

    @Test
    @DisplayName("toDocument y toDomain - preservan el evento y los items embebidos")
    void toDocumentYToDomain_preservanCampos() {
        UUID idPedido = UUID.randomUUID();
        ItemPedido item = ItemPedido.builder().idPlato(1L).nombrePlato("Hamburguesa")
                .precioCongelado(20000.0).cantidad(2).build();
        EventoPedido evento = EventoPedido.builder()
                .idPedido(idPedido).idMesa(4L).tipo("CAMBIO_ESTADO")
                .estadoAnterior(EstadoPedido.RECIBIDO).estadoNuevo(EstadoPedido.EN_PREPARACION)
                .timestamp(LocalDateTime.now()).items(List.of(item)).build();

        EventoPedidoDocument documento = mapper.toDocument(evento);
        EventoPedido resultado = mapper.toDomain(documento);

        assertEquals(idPedido.toString(), documento.getIdPedido());
        assertEquals(1, documento.getItems().size());
        assertEquals(idPedido, resultado.getIdPedido());
        assertEquals(EstadoPedido.EN_PREPARACION, resultado.getEstadoNuevo());
        assertEquals(1, resultado.getItems().size());
        assertEquals("Hamburguesa", resultado.getItems().get(0).getNombrePlato());
        assertEquals(20000.0, resultado.getItems().get(0).getPrecioCongelado());
    }
}
