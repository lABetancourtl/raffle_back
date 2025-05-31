package com.aba.raffle.proyecto.dto;

import com.aba.raffle.proyecto.model.enums.EstadoNumber;

public record CambiarStateNumberDTO(
        String numero,
        EstadoNumber nuevoEstado
) {
}
