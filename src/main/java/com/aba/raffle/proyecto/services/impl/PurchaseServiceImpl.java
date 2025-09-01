package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.dto.NumeroDTO;
import com.aba.raffle.proyecto.mappers.NumberMapper;
import com.aba.raffle.proyecto.model.entities.NumberRaffle;
import com.aba.raffle.proyecto.model.entities.PaymentOperation;
import com.aba.raffle.proyecto.model.entities.Raffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.vo.Buyer;
import com.aba.raffle.proyecto.repositories.NumberRepository;
import com.aba.raffle.proyecto.repositories.PaymentOperationRepository;
import com.aba.raffle.proyecto.repositories.RaffleRepository;
import com.aba.raffle.proyecto.services.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final NumberRepository numberRepository;
    private final RaffleRepository raffleRepository;
    private final PaymentOperationRepository paymentOperationRepository;
    private final NumberMapper numberMapper;

    @Override
    public void comprarNumero(BuyRequestDTO buyRequestDTO, String externalReference) {
        Long raffleObjectId = Long.valueOf(buyRequestDTO.raffleId());


        // Obtener números aleatorios disponibles
        List<NumberRaffle> randomNumbers = numberRepository.findRandomAvailableNumbers(
                raffleObjectId,
                EstadoNumber.DISPONIBLE,
                buyRequestDTO.quantity()
        );
//        System.out.println("Cantidad que llega desde el front: "+ buyRequestDTO.quantity());
//        System.out.println("Lista de numeros disponibles random: "+randomNumbers);

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
            number.setStateNumber(EstadoNumber.RESERVADO);
            number.setBuyer(buyer);
            number.setReservedAt(LocalDateTime.now());
            number.setPaymentSessionId(externalReference);
        });

        numberRepository.saveAll(randomNumbers);
    }

    @Override
    public Optional<Integer> obtenerCantidadNumerosDisponibles(String idRaffle) {
        Long raffleObjectId = Long.valueOf(idRaffle);
        List<NumberRaffle> numerosDisponibles = numberRepository.findByStateNumberAndRaffleId(EstadoNumber.DISPONIBLE, raffleObjectId);
        Optional<Integer> cantidadNumerosDisponibles = Optional.of(numerosDisponibles.size());
        return cantidadNumerosDisponibles;
    }

    @Override
    public List<NumeroDTO> asignarNumerosAleatoriosDesdeAdmin(BuyRequestDTO buyRequestDTO) {
        Long raffleObjectId = Long.valueOf(buyRequestDTO.raffleId());
        Optional<Raffle> raffle = raffleRepository.findById(raffleObjectId);

        // Obtener números aleatorios disponibles
        List<NumberRaffle> randomNumbers = numberRepository.findRandomAvailableNumbers(
                raffleObjectId,
                EstadoNumber.DISPONIBLE,
                buyRequestDTO.quantity()
        );

        if (randomNumbers.size() < buyRequestDTO.quantity()) {
            throw new RuntimeException("No hay suficientes números disponibles para esta rifa.");
        }

        Buyer buyer = Buyer.builder()
                .name(buyRequestDTO.buyerName())
                .apellido(buyRequestDTO.buyerApellido())
                .pais(buyRequestDTO.buyerPais())
                .email(buyRequestDTO.buyerEmail())
                .prefix(buyRequestDTO.buyerPrefix())
                .phone(buyRequestDTO.buyerPhone())
                .build();

        for (NumberRaffle number : randomNumbers) {
            number.setStateNumber(EstadoNumber.VENDIDO); // Puedes usar RESERVADO si prefieres
            number.setBuyer(buyer);
            number.setReservedAt(LocalDateTime.now());
            number.setPaymentSessionId("ADMIN-" + UUID.randomUUID()); // Marcador para diferenciar
        }


        PaymentOperation op = new PaymentOperation();
        op.setPaymentId("Pago Presencial");
        op.setStatus("approved");
        op.setMonto((raffle.get().getPriceNumber()).doubleValue() * buyRequestDTO.quantity());
        op.setMoneda("COP");
        op.setMetodoPago("Contado");
        op.setFechaPago(LocalDateTime.now());
        op.setExternalReference(null);
        op.setCompradorEmail(buyer.getName());
        op.setRawPayload(null);
        op.setRegistradoEn(LocalDateTime.now());
        op.setExpirada(false);
        op.setRaffleId(raffleObjectId.toString());
        op.setCantidadNumeros(randomNumbers.size());
        op.setCompradorNombre(buyer.getName());
        op.setCompradorApellido(buyer.getApellido());
        op.setCompradorPais(buyer.getPais());
        op.setCompradorTelefono(buyer.getPhone());
        op.setNumerosComprados(randomNumbers.stream().map(NumberRaffle::getNumber).toList());

        paymentOperationRepository.save(op);

        numberRepository.saveAll(randomNumbers);
        return randomNumbers.stream()
                .map(NumberRaffle::getNumber)
                .map(n -> n.contains("_") ? n.split("_")[1] : n)
                .map(NumeroDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public NumeroDTO asignarNumeroDesdeAdmin(BuyRequestDTO buyRequestDTO, NumeroDTO numeroDTO) {
        Long raffleObjectId = Long.valueOf(buyRequestDTO.raffleId());
        Optional<Raffle> raffle = raffleRepository.findById(raffleObjectId);

        String numeroCompleto = raffleObjectId + "_" + numeroDTO.numero();

        NumberRaffle numero = numberRepository.findByNumberAndStateNumber(numeroCompleto, EstadoNumber.DISPONIBLE);
        if (numero != null ) {
            Buyer buyer = Buyer.builder()
                    .name(buyRequestDTO.buyerName())
                    .apellido(buyRequestDTO.buyerApellido())
                    .pais(buyRequestDTO.buyerPais())
                    .email(buyRequestDTO.buyerEmail())
                    .prefix(buyRequestDTO.buyerPrefix())
                    .phone(buyRequestDTO.buyerPhone())
                    .build();

            numero.setStateNumber(EstadoNumber.VENDIDO);
            numero.setBuyer(buyer);
            numero.setReservedAt(LocalDateTime.now());
            numero.setPaymentSessionId("ADMIN-" + UUID.randomUUID());


            PaymentOperation op = new PaymentOperation();
            op.setPaymentId("Pago Presencial");
            op.setStatus("approved");
            op.setMonto((raffle.get().getPriceNumber()).doubleValue() * buyRequestDTO.quantity());
            op.setMoneda("COP");
            op.setMetodoPago("Contado");
            op.setFechaPago(LocalDateTime.now());
            op.setExternalReference(null);
            op.setCompradorEmail(buyer.getName());
            op.setRawPayload(null);
            op.setRegistradoEn(LocalDateTime.now());
            op.setExpirada(false);
            op.setRaffleId(raffleObjectId.toString());
            op.setCantidadNumeros(1);
            op.setCompradorNombre(buyer.getName());
            op.setCompradorApellido(buyer.getApellido());
            op.setCompradorPais(buyer.getPais());
            op.setCompradorTelefono(buyer.getPhone());


            String valor = numero.getNumber();
            String procesado = valor != null && valor.contains("_")
                    ? valor.split("_")[1]
                    : valor;

            op.setNumerosComprados(List.of(procesado));

            paymentOperationRepository.save(op);
            numberRepository.save(numero);
            return new NumeroDTO(procesado);

        } else {

        }
        return null;
    }


}
