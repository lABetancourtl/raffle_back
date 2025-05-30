package com.aba.raffle.proyecto.services;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import jakarta.validation.Valid;

import java.util.List;

public interface PurchaseService {
    void comprarNumero(@Valid BuyRequestDTO buyRequestDTO);
    List<NumberRaffle> obtenerNumerosPorEmail(String email);

}
