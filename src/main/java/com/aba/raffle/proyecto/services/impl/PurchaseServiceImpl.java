package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.mappers.NumberMapper;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.vo.Buyer;
import com.aba.raffle.proyecto.repositories.NumberRepository;
import com.aba.raffle.proyecto.services.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final NumberRepository numberRepository;
    private final NumberMapper numberMapper;

    @Override
    public void comprarNumero(BuyRequestDTO buyRequestDTO) {
        ObjectId raffleObjectId = new ObjectId(buyRequestDTO.raffleId());

        // Obtener números aleatorios disponibles
        List<NumberRaffle> randomNumbers = numberRepository.findRandomAvailableNumbers(
                raffleObjectId,
                EstadoNumber.DISPONIBLE,
                buyRequestDTO.quantity()
        );

        if (randomNumbers.size() < buyRequestDTO.quantity()) {
            throw new RuntimeException("No hay suficientes números disponibles");
        }

        Buyer buyer = Buyer.builder()
                .name(buyRequestDTO.buyerName())
                .apellido(buyRequestDTO.buyerApellido())
                .pais(buyRequestDTO.buyerPais())
                .email(buyRequestDTO.buyerEmail())
                .prefix(buyRequestDTO.buyerPrefix())
                .phone(buyRequestDTO.buyerPhone())
                .build();

        randomNumbers.forEach(number -> {
            number.setStateNumber(EstadoNumber.VENDIDO);
            number.setBuyer(buyer);
        });

        numberRepository.saveAll(randomNumbers);
    }

    @Override
    public Optional<Integer> obtenerCantidadNumerosDisponibles(String idRaffle) {
        ObjectId raffleObjectId = new ObjectId(idRaffle);
        List<NumberRaffle> numerosDisponibles = numberRepository.findByStateNumberAndRaffleId(EstadoNumber.DISPONIBLE, raffleObjectId);
        Optional<Integer> cantidadNumerosDisponibles = Optional.of(numerosDisponibles.size());
        System.out.println(cantidadNumerosDisponibles);
        return cantidadNumerosDisponibles;
    }

}
