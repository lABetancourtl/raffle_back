package com.aba.raffle.proyecto.controllers;


import com.aba.raffle.proyecto.dto.MensajeDTO;
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

    @PostMapping("/procesar-pago")
    public ResponseEntity<MensajeDTO<String>> procesarPago(@RequestBody Map<String, Object> datosPago) {
        // 🔹 Aquí puedes registrar la operación en tu base de datos o reenviar al webhook si quieres
        System.out.println("Procesando pago manual: " + datosPago);

        return ResponseEntity.ok(new MensajeDTO<>(false, "Pago procesado correctamente"));
    }





}