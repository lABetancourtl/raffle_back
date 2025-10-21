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

import java.util.*;
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

    @Override
    public List<NumeroDTO> ejecutarSorteo(Long raffleId) {
        // 1️⃣ Obtener la rifa
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rifa no encontrada"));

        // 2️⃣ Validar que esté FINALIZADA
        if (raffle.getStateRaffle() != EstadoRaffle.FINALIZADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La rifa debe estar finalizada para ejecutar el sorteo");
        }

        // 3️⃣ Obtener todos los números vendidos
        List<NumberRaffle> vendidos = numberRepository.findByStateNumberAndRaffleId(EstadoNumber.VENDIDO, raffleId);
        if (vendidos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay boletos vendidos");
        }

        // 4️⃣ Seleccionar ganador aleatoriamente
        int totalGanadores = 1; // Puedes parametrizarlo si el front permite varios
        List<NumeroDTO> ganadores = new ArrayList<>();

        for (int i = 0; i < totalGanadores; i++) {
            NumberRaffle ganador = vendidos.get((int) (Math.random() * vendidos.size()));
            String numero = ganador.getNumber().contains("_")
                    ? ganador.getNumber().split("_")[1]
                    : ganador.getNumber();

            ganadores.add(new NumeroDTO(numero));
        }

        // 5️⃣ (Opcional) Podrías guardar el resultado en una tabla "winners" o enviar correo al ganador.

        return ganadores;
    }




    @Override
    public List<WinnerDTO> ejecutarSorteo(Long raffleId, int numeroGanadores) {
        // Verifica que la rifa exista
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new RuntimeException("Rifa no encontrada"));

        // Trae todos los boletos VENDIDOS de esa rifa (USANDO numberRepository ✅)
        List<NumberRaffle> vendidos =
                numberRepository.findByStateNumberAndRaffleId(EstadoNumber.VENDIDO, raffleId);

        if (vendidos == null || vendidos.isEmpty()) {
            throw new RuntimeException("No hay boletos vendidos para esta rifa");
        }

        // Mezcla aleatoriamente y toma la cantidad solicitada
        Collections.shuffle(vendidos);
        List<NumberRaffle> seleccionados = vendidos.stream()
                .limit(Math.max(1, numeroGanadores))
                .collect(Collectors.toList());

        // Mapea a DTO para el front (usando campos reales de Buyer)
        List<WinnerDTO> ganadores = seleccionados.stream()
                .map(n -> {
                    String numeroCompleto = n.getNumber();                 // ej: "15_0023"
                    String soloNumero = numeroCompleto.contains("_")
                            ? numeroCompleto.split("_")[1]
                            : numeroCompleto;

                    Buyer b = n.getBuyer();
                    return new WinnerDTO(
                            soloNumero,                     // numero sin prefijo
                            b != null ? b.getName() : "",
                            b != null ? b.getApellido() : "",
                            b != null ? b.getEmail() : "",
                            b != null ? b.getPhone() : ""
                    );
                })
                .collect(Collectors.toList());

        return ganadores;
    }
}
