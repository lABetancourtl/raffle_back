package com.aba.raffle.proyecto.conotrollers;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.dto.MensajeDTO;
import com.aba.raffle.proyecto.dto.UserCreateDTO;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.services.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping("/comprarNumero")
    public ResponseEntity<MensajeDTO<String>> comprarNumero(@Valid @RequestBody BuyRequestDTO buyRequestDTO) throws Exception{
        purchaseService.comprarNumero(buyRequestDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Pago exitoso"));
    }

    @GetMapping("/numerosPorEmail")
    public ResponseEntity<List<NumberRaffle>> obtenerNumerosPorEmail(@RequestParam String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email no válido");
        }

        List<NumberRaffle> numeros = purchaseService.obtenerNumerosPorEmail(email);
        return ResponseEntity.ok(numeros);
    }


}
