package com.aba.raffle.proyecto.conotrollers;


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
    public Map<String, String> crearPreferencia(@RequestBody Map<String, Object> payload) throws Exception {
        String descripcion = (String) payload.get("descripcion");
        int cantidad = (int) payload.get("cantidad");
        double precio = Double.parseDouble(payload.get("precio").toString());
        String email = (String) payload.get("email");

        String preferenceId = mercadoPagoService.crearPreferenciaPago(descripcion, cantidad, precio, email);
        return Map.of("preferenceId", preferenceId);
    }
}