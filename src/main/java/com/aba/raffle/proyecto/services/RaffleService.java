package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.RaffleCreateDTO;
import jakarta.validation.Valid;

public interface RaffleService {
    void crearRifa(@Valid RaffleCreateDTO raffleCreate);
}
