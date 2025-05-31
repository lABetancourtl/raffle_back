package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.*;
import com.aba.raffle.proyecto.mappers.RaffleMapper;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.model.documents.Raffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.vo.Buyer;
import com.aba.raffle.proyecto.repositories.NumberRepository;
import com.aba.raffle.proyecto.repositories.RaffleRepository;
import com.aba.raffle.proyecto.services.RaffleService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RaffleServiceImpl implements RaffleService {
    private final RaffleRepository raffleRepository;
    private final RaffleMapper raffleMapper;
    private final NumberRepository numberRepository;


    @Override
    public void crearRifa(RaffleCreateDTO raffleCreate) {
        // 1. Crear y guardar la rifa
        Raffle raffle = raffleMapper.fromCreateRaffleDTO(raffleCreate);
        raffleRepository.save(raffle); // aquí raffle ya tiene su ID generado

        // 2. Calcular cantidad total de números (10^digitLength)
        int digitLength = raffleCreate.digitLength();
        int totalNumbers = (int) Math.pow(10, digitLength);
        List<NumberRaffle> numbers = new ArrayList<>(totalNumbers);
        for (int i = 0; i < totalNumbers; i++) {
            String formattedNumber = String.format("%0" + digitLength + "d", i);
            NumberRaffle numberRaffle = NumberRaffle.builder()
                    .number(formattedNumber)
                    .stateNumber(EstadoNumber.DISPONIBLE)
                    .buyer(null)
                    .raffleId(raffle.getId())
                    .build();
            numbers.add(numberRaffle);
        }

        // 3. Guardar todos los números en la colección numbers
        numberRepository.saveAll(numbers);
    }

    @Override
    public List<NumberRaffle> obtenerNumerosPorEmail(String email) {
        return numberRepository.findByBuyerEmail(email);
    }

    @Override
    public ResultadoBuyerDTO obtenerClientePorNumero(String numero) {
        Optional<NumberRaffle> numberOpt = numberRepository.findByNumber(numero);

        if (numberOpt.isEmpty()) {
            return new ResultadoBuyerDTO("Número no existe", null);
        }

        NumberRaffle numberRaffle = numberOpt.get();

        if (numberRaffle.getBuyer() == null) {
            return new ResultadoBuyerDTO("Número aún no comprado", null);
        }

        Buyer buyer = numberRaffle.getBuyer();

        BuyerDTO buyerDTO = new BuyerDTO(
                buyer.getName() + " " + buyer.getApellido(),
                buyer.getEmail(),
                buyer.getPrefix() + " " + buyer.getPhone(),
                buyer.getPais()
        );

        return new ResultadoBuyerDTO("Cliente encontrado", buyerDTO);
    }

    @Override
    public void cambiarStateNumber(CambiarStateNumberDTO cambiarStateNumberDTO) {
        NumberRaffle numberRaffle = numberRepository.findByNumber(cambiarStateNumberDTO.numero())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Número no encontrado"));

        numberRaffle.setStateNumber(cambiarStateNumberDTO.nuevoEstado());
        numberRepository.save(numberRaffle);
    }

    @Override
    public List<NumberRaffle> obtenerNumerosPorEstado(EstadoNumber estado) {
        List<NumberRaffle> numeros = numberRepository.findByStateNumber(estado);
        return numeros;
    }

    @Override
    public void cambiarStateRaffle(CambiarStateRaffleDTO cambiarStateRaffleDTO) {
        ObjectId idRaffle = new ObjectId(cambiarStateRaffleDTO.id());
        Raffle raffle = raffleRepository.findById(idRaffle).
                orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rifa no encontrada"));
        raffle.setStateRaffle(cambiarStateRaffleDTO.nuevoEstado());
        raffleRepository.save(raffle);
    }


}
