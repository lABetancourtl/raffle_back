package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.CambiarStateNumberDTO;
import com.aba.raffle.proyecto.dto.CambiarStateRaffleDTO;
import com.aba.raffle.proyecto.dto.RaffleCreateDTO;
import com.aba.raffle.proyecto.dto.ResultadoBuyerDTO;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.model.documents.Raffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface RaffleService {
    void crearRifa(@Valid RaffleCreateDTO raffleCreate);
    List<NumberRaffle> obtenerNumerosPorEmail(String email);
    ResultadoBuyerDTO obtenerClientePorNumero(String numero);

    void cambiarStateNumber(@Valid CambiarStateNumberDTO cambiarStateNumberDTO);

    List<NumberRaffle> obtenerNumerosPorEstado(EstadoNumber estado);

    void cambiarStateRaffle(@Valid CambiarStateRaffleDTO cambiarStateRaffleDTO);

    List<Raffle> obtenerTodasLasRifas();

    Optional<Raffle> obtenerRifaActiva();

    List<String> obtenerSoloNumerosPorEmail(String email);
}
