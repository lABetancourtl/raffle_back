package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.PagoRequestDTO;

import java.util.Map;

public interface WompiService {

    Map<String, String> iniciarProcesoDePagoConWompi(PagoRequestDTO datosPago) throws Exception;

    void procesarPago(Map<String, Object> payload);
}
