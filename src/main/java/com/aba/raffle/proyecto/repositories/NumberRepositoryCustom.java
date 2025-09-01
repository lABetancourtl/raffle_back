package com.aba.raffle.proyecto.repositories;

import com.aba.raffle.proyecto.model.entities.NumberRaffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;

import java.util.List;

public interface NumberRepositoryCustom {
    List<NumberRaffle> findRandomAvailableNumbers(Long raffleId, EstadoNumber estado, int quantity);
}
