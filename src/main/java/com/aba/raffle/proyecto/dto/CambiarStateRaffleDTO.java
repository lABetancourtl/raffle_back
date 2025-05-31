package com.aba.raffle.proyecto.dto;

import com.aba.raffle.proyecto.model.enums.EstadoRaffle;

public record CambiarStateRaffleDTO(
        String id,
        EstadoRaffle nuevoEstado
) {
}
