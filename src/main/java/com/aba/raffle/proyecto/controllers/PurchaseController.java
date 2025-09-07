package com.aba.raffle.proyecto.controllers;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.dto.MensajeDTO;
import com.aba.raffle.proyecto.repositories.NumberRepositoryCustomImpl;
import com.aba.raffle.proyecto.services.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final NumberRepositoryCustomImpl numberRepository;

    @PostMapping("/comprarNumero")
    public ResponseEntity<MensajeDTO<String>> comprarNumero(@Valid @RequestBody BuyRequestDTO buyRequestDTO) throws Exception{
        purchaseService.comprarNumero(buyRequestDTO, null);
        return ResponseEntity.ok(new MensajeDTO<>(false,"Pago exitoso"));
    }

    @GetMapping("/cantidadNumerosDisponibles")
    public ResponseEntity<MensajeDTO<Integer>> obtenerCantidadNumerosDisponibles(@RequestParam String idRaffle) {
        int cantidadNumerosDisponibles = purchaseService.obtenerCantidadNumerosDisponibles(idRaffle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento sin numeros asignados"));
        return ResponseEntity.ok(new MensajeDTO<>(false, cantidadNumerosDisponibles));
    }



//    @GetMapping("/cantidad-disponible")
//    public ResponseEntity<MensajeDTO<Integer>> obtenerCantidadDisponible(@RequestParam String raffleId) {
//        ObjectId raffleObjectId = new ObjectId(raffleId);
//        int cantidadDisponible = numberRepository.findRandomAvailableNumbers(raffleObjectId, EstadoNumber.DISPONIBLE);
//        return ResponseEntity.ok(new MensajeDTO<>(false, cantidadDisponible));
//    }





}
