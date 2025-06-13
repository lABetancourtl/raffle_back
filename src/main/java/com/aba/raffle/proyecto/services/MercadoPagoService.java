package com.aba.raffle.proyecto.services;

public interface MercadoPagoService {
    String crearPreferenciaPago(String descripcion, int cantidad, double precio, String email) throws Exception;
}
