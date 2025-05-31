package com.aba.raffle.proyecto.conotrollers;


import com.aba.raffle.proyecto.dto.*;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.enums.EstadoRaffle;
import com.aba.raffle.proyecto.services.RaffleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/raffle")
public class RaffleController {

    private final RaffleService raffleService;


    @PostMapping("/crearRifa")
    public ResponseEntity<MensajeDTO<String>> crearRifa(@Valid @RequestBody RaffleCreateDTO raffleCreate) throws Exception{
        raffleService.crearRifa(raffleCreate);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Rifa creada exitosamente"));
    }

    @GetMapping("/numerosPorEmail")
    public ResponseEntity<List<NumberRaffle>> obtenerNumerosPorEmail(@RequestParam String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email no válido");
        }
        List<NumberRaffle> numeros = raffleService.obtenerNumerosPorEmail(email);
        return ResponseEntity.ok(numeros);
    }

    @GetMapping("/clientePorNumero")
    public ResponseEntity<MensajeDTO<ResultadoBuyerDTO>> obtenerClientePorNumero(@Valid @RequestParam String numero) throws Exception{
        if (!numero.matches("^[0-9]{1,4}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número no válido");
        }
        ResultadoBuyerDTO resultado = raffleService.obtenerClientePorNumero(numero);
        return ResponseEntity.ok(new MensajeDTO<>(false, resultado));
    }

    @PatchMapping("/cambiarEstadoNumero")
    public ResponseEntity<MensajeDTO<String>> cambiarStateNumber(@Valid @RequestBody CambiarStateNumberDTO cambiarStateNumberDTO) throws Exception{
        raffleService.cambiarStateNumber(cambiarStateNumberDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Estado cambiado correctamente"));
    }

    @GetMapping("/numerosPorEstado")
    public ResponseEntity<List<NumberRaffle>> obtenerNumerosPorEstado(@RequestParam String estadoNumber) {
        EstadoNumber estado;
        try {
            estado = EstadoNumber.valueOf(estadoNumber.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido");
        }
        List<NumberRaffle> numeros = raffleService.obtenerNumerosPorEstado(estado);

        return ResponseEntity.ok(numeros);
    }

    @PatchMapping("/cambiarEstadoRifa")
    public ResponseEntity<MensajeDTO<String>> cambiarStateRaffle(@Valid @RequestBody CambiarStateRaffleDTO cambiarStateRaffleDTO) throws Exception{
        raffleService.cambiarStateRaffle(cambiarStateRaffleDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Estado cambiado correctamente"));
    }


}
