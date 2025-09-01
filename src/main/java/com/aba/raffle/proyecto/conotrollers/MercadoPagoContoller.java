package com.aba.raffle.proyecto.conotrollers;


import com.aba.raffle.proyecto.dto.PagoRequestDTO;
import com.aba.raffle.proyecto.services.MercadoPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mercadopago")
public class MercadoPagoContoller {

    private final MercadoPagoService mercadoPagoService;

    @PostMapping("/crear-preferencia")
    public ResponseEntity<Map<String, String>> iniciarProcesoPago(@RequestBody PagoRequestDTO datosPago) throws Exception {
        Map<String, String> respuesta = mercadoPagoService.iniciarProcesoDePago(datosPago);
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Webhook recibido: " + payload);
        mercadoPagoService.procesarPago(payload);
        return ResponseEntity.ok("Webhook recibido");
    }




}