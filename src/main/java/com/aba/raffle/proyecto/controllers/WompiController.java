package com.aba.raffle.proyecto.controllers;

import com.aba.raffle.proyecto.dto.PagoRequestDTO;
import com.aba.raffle.proyecto.dto.WompiTransaccionDTO;
import com.aba.raffle.proyecto.services.WompiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wompi")
public class WompiController {

    private final WompiService wompiService;

    @PostMapping("/crear-transaccion")
    public ResponseEntity<Map<String, String>> iniciarProcesoPagoConWompi(
            @RequestBody PagoRequestDTO datosPago
    ) throws Exception {
        Map<String, String> respuesta = wompiService.iniciarProcesoDePagoConWompi(datosPago);
        return ResponseEntity.ok(respuesta);
    }




    @PostMapping("/webhook")
    public ResponseEntity<String> recibirWebhook(@RequestBody Map<String, Object> payload) {
        wompiService.procesarPago(payload);
        return ResponseEntity.ok("Webhook recibido");
    }
}
