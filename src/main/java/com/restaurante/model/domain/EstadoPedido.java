package com.restaurante.model.domain;

/**
 * Estados por los que pasa un pedido.
 *
 * <p>La máquina de estados vive aquí, en el dominio: nadie más conoce las transiciones
 * (ni el Service, ni el Validator, ni el Controller).</p>
 *
 * <p><b>Nota de diseño (decisión justificada):</b> la guía de clase mostraba un ejemplo
 * simplificado donde solo {@code RECIBIDO} podía cancelarse. En este restaurante un pedido
 * también se puede cancelar en {@code EN_PREPARACION} (el cliente se arrepiente, se agota un
 * ingrediente, la mesa se levanta), pero nunca si ya está {@code LISTO} o {@code ENTREGADO}.
 * Las dos consecuencias de esa decisión están encapsuladas aquí:</p>
 * <ul>
 *   <li>{@link #esCancelable()} es exactamente equivalente a poder transicionar a
 *       {@code CANCELADO} ({@code RECIBIDO} y {@code EN_PREPARACION}).</li>
 *   <li>{@link #esFinal()} marca los estados terminales ({@code ENTREGADO} y {@code CANCELADO});
 *       lo usa {@code PlatoValidatorImpl} para saber si un plato tiene pedidos activos
 *       antes de permitir borrarlo.</li>
 * </ul>
 */
public enum EstadoPedido {
    RECIBIDO,
    EN_PREPARACION,
    LISTO,
    ENTREGADO,
    CANCELADO;

    public boolean puedeTransicionarA(EstadoPedido siguiente) {
        return switch (this) {
            case RECIBIDO -> siguiente == EN_PREPARACION || siguiente == CANCELADO;
            case EN_PREPARACION -> siguiente == LISTO || siguiente == CANCELADO;
            case LISTO -> siguiente == ENTREGADO;
            default -> false;
        };
    }

    /**
     * Un pedido solo se puede cancelar mientras la cocina no lo haya terminado.
     * Es equivalente a {@code puedeTransicionarA(CANCELADO)}.
     */
    public boolean esCancelable() {
        return this == RECIBIDO || this == EN_PREPARACION;
    }

    /**
     * Estado terminal: el pedido ya no vuelve a cambiar. Un pedido en estado final
     * no cuenta como "pedido activo" para un plato.
     */
    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }
}
