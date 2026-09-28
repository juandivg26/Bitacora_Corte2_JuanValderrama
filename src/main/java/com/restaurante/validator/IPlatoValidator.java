package com.restaurante.validator;

public interface IPlatoValidator {

    /**
     * Verifica que un plato no este referenciado por items de pedidos activos
     * (pedidos que aun no estan ENTREGADOS ni CANCELADOS).
     *
     * @param idPlato plato que se quiere eliminar
     */
    void validarSinPedidosActivos(Long idPlato);

    void validarNombreUnico(String nombre);
}
