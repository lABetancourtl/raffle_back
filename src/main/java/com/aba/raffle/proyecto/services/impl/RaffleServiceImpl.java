package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.*;
import com.aba.raffle.proyecto.mappers.PaymentOperationMapper;
import com.aba.raffle.proyecto.mappers.RaffleMapper;
import com.aba.raffle.proyecto.model.entities.NumberRaffle;
import com.aba.raffle.proyecto.model.entities.PaymentOperation;
import com.aba.raffle.proyecto.model.entities.Raffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.enums.EstadoRaffle;
import com.aba.raffle.proyecto.model.vo.Buyer;
import com.aba.raffle.proyecto.repositories.NumberRepository;
import com.aba.raffle.proyecto.repositories.PaymentOperationRepository;
import com.aba.raffle.proyecto.repositories.RaffleRepository;
import com.aba.raffle.proyecto.services.RaffleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RaffleServiceImpl implements RaffleService {
    private final RaffleRepository raffleRepository;
    private final RaffleMapper raffleMapper;
    private final NumberRepository numberRepository;
    private final PaymentOperationRepository paymentOperationRepository;
    private final PaymentOperationMapper paymentOperationMapper;


    @Override
    public void crearRifa(RaffleCreateDTO raffleCreate) {
        // 1. Crear y guardar la rifa
        Raffle raffle = raffleMapper.fromCreateRaffleDTO(raffleCreate);
        raffleRepository.save(raffle); // aquí raffle ya tiene su ID generado

        // 2. Calcular cantidad total de números (10^digitLength)
        int digitLength = raffle.getDigitLength();
        int totalNumbers = (int) Math.pow(10, digitLength);
        List<NumberRaffle> numbers = new ArrayList<>(totalNumbers);
        for (int i = 0; i < totalNumbers; i++) {
            String formattedNumber = String.format("%0" + digitLength + "d", i);
            NumberRaffle numberRaffle = NumberRaffle.builder()
                    .number(raffle.getId() + "_" + formattedNumber) // ID único basado en rifa + número
                    .stateNumber(EstadoNumber.DISPONIBLE)
                    .buyer(null)
                    .raffleId(raffle.getId())
                    .paymentSessionId(null)
                    .reservedAt(null)
                    .build();
            numbers.add(numberRaffle);
        }
        System.out.println("Todos los numeros: " + numbers.get(0).getNumber());
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
        Long idRaffle = Long.valueOf(cambiarStateRaffleDTO.id());
        Raffle raffle = raffleRepository.findById(idRaffle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rifa no encontrada"));

        EstadoRaffle nuevoEstado = cambiarStateRaffleDTO.nuevoEstado();

        if (nuevoEstado == EstadoRaffle.ACTIVO) {
            // Buscar si ya hay una rifa activa (que no sea la misma)
            Optional<Raffle> rifaActiva = raffleRepository.findByStateRaffle(EstadoRaffle.ACTIVO);
            if (rifaActiva.isPresent() && !rifaActiva.get().getId().equals(raffle.getId())) {
                Raffle otraRifa = rifaActiva.get();
                otraRifa.setStateRaffle(EstadoRaffle.PAUSA);
                raffleRepository.save(otraRifa);
            }
        }

        // Actualiza el estado de la rifa actual
        raffle.setStateRaffle(nuevoEstado);
        raffleRepository.save(raffle);
    }

    @Override
    public List<Raffle> obtenerTodasLasRifas() {
        List<Raffle> rifas = raffleRepository.findAll();

        if (rifas.isEmpty()) {
            return rifas;
        }

        // Obtenemos todos los IDs de las rifas
        List<Long> rifaIds = rifas.stream()
                .map(Raffle::getId)
                .collect(Collectors.toList());

        // Obtenemos todos los números vendidos de todas las rifas en una sola consulta
        List<NumberRaffle> todosNumerosVendidos = numberRepository.findByStateNumberAndRaffleIdIn(EstadoNumber.VENDIDO, rifaIds);

        List<NumberRaffle> numerosD = numberRepository.findByStateNumberAndRaffleIdIn(EstadoNumber.DISPONIBLE, rifaIds);
        int numerosDisponibles = numerosD.size();



        // Creamos un mapa de conteo por rifa
        Map<Long, Long> conteoPorRifa = todosNumerosVendidos.stream()
                .collect(Collectors.groupingBy(
                        NumberRaffle::getRaffleId,
                        Collectors.counting()
                ));

        // Calculamos el porcentaje para cada rifa
        return rifas.stream()
                .map(raffle -> {
                    Long numerosVendidos = conteoPorRifa.getOrDefault(raffle.getId(), 0L);
                    int totalNumerosPosibles = (int) Math.pow(10, raffle.getDigitLength());

                    int porcentajeVendidos = totalNumerosPosibles > 0
                            ? (int) (numerosVendidos * 100 / totalNumerosPosibles)
                            : 0;

                    raffle.setPorcentajeVendidos(porcentajeVendidos);
                    raffle.setCantidadDisponibles(numerosDisponibles);
                    return raffle;
                })
                .collect(Collectors.toList());

    }

    @Override
    public Optional<Raffle> obtenerRifaActiva() {
        Optional<Raffle> raffleOpt = raffleRepository.findByStateRaffle(EstadoRaffle.ACTIVO);

        if (raffleOpt.isEmpty()) {
            return Optional.empty();
        }

        Raffle raffle = raffleOpt.get();
        Long idRaffle = raffle.getId(); // <-- usa el ObjectId real

        // Obtenemos los números vendidos de esa rifa
        List<NumberRaffle> numerosV = numberRepository.findByStateNumberAndRaffleId(EstadoNumber.VENDIDO, idRaffle);
        int numerosVendidos = numerosV.size();

        List<NumberRaffle> numerosD = numberRepository.findByStateNumberAndRaffleId(EstadoNumber.DISPONIBLE, idRaffle);
        int numerosDisponibles = numerosD.size();

        // Calculamos el total de números posibles según los dígitos
        int totalNumerosPosibles = (int) Math.pow(10, raffle.getDigitLength());

        // Calculamos el porcentaje vendido (evitar división por cero)
        int porcentajeVendidos = totalNumerosPosibles > 0
                ? (numerosVendidos * 100) / totalNumerosPosibles
                : 0;

        // Asignamos el valor al campo @Transient
        raffle.setPorcentajeVendidos(porcentajeVendidos);
        raffle.setCantidadDisponibles(numerosDisponibles);

        return Optional.of(raffle);
    }

    /*Este metodo se encarga de retornar solo el numero de la rifa el cual esta despues del _ en el campo asignado como @Id del document en mongo.
    Ej:  este es el @Id 685debac1a01612f9b97836c_00 y el metodo retorna solo 00
    */
    @Override
    public List<NumeroDTO> obtenerSoloNumerosPorEmail(String email) {
        return numberRepository.findByBuyerEmail(email)
                .stream()
                .map(NumberRaffle::getNumber)
                .map(n -> n.contains("_") ? n.split("_")[1] : n)
                .map(NumeroDTO::new)
                .collect(Collectors.toList());
    }



    @Override
    public List<PaymentOperationDTO> getOperacionesByRaffle(String raffleId) {
        List<PaymentOperation> operaciones = paymentOperationRepository.findByRaffleId(raffleId);
        List<PaymentOperationDTO> listaOperationes = paymentOperationMapper.toDtoList(operaciones);
        return listaOperationes;
    }



}
