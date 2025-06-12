package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface PurchaseService {
    void comprarNumero(@Valid BuyRequestDTO buyRequestDTO);

    Optional<Integer> obtenerCantidadNumerosDisponibles(@Valid String idRaffle);
}
