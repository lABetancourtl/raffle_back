package com.aba.raffle.proyecto.conotrollers;


import com.aba.raffle.proyecto.dto.MensajeDTO;
import com.aba.raffle.proyecto.dto.RaffleCreateDTO;
import com.aba.raffle.proyecto.dto.UserCreateDTO;
import com.aba.raffle.proyecto.services.RaffleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
