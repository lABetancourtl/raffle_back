package com.aba.raffle.proyecto.dto;

import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.vo.Buyer;

public record NumberDTO(
        int number,
        EstadoNumber stateNumber,
        Buyer buyer, //falta validar si aun no tiene comprador
        String raffleId
) {
}
