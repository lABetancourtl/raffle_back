package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.PagoRequestDTO;

import java.util.Map;

public interface MercadoPagoService {

    void procesarPago(Map<String, Object> payload);

    Map<String, String> iniciarProcesoDePago(PagoRequestDTO datosPago) throws Exception;
}
