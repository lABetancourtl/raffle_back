package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.*;
import com.aba.raffle.proyecto.mappers.PaymentOperationMapper;
import com.aba.raffle.proyecto.mappers.RaffleMapper;
import com.aba.raffle.proyecto.model.entities.*;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.enums.EstadoRaffle;
import com.aba.raffle.proyecto.model.vo.Buyer;
import com.aba.raffle.proyecto.repositories.NumberRepository;
import com.aba.raffle.proyecto.repositories.PaymentOperationRepository;
import com.aba.raffle.proyecto.repositories.RaffleRepository;
import com.aba.raffle.proyecto.repositories.SorteoActaRepository;
import com.aba.raffle.proyecto.services.RaffleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Comparator;

import java.time.LocalDateTime;
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



    @Autowired
    private SorteoActaRepository sorteoActaRepository;
    @Autowired
    private EmailService emailService;

    @Override
    public ResultadoSorteoDTO ejecutarSorteo(Long raffleId, int numeroGanadores) throws Exception {
        // 1️⃣ Verifica que la rifa exista
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new RuntimeException("Rifa no encontrada"));

        // 2️⃣ Obtiene los boletos vendidos
        List<NumberRaffle> vendidos = numberRepository.findByStateNumberAndRaffleId(EstadoNumber.VENDIDO, raffleId);
        if (vendidos == null || vendidos.isEmpty()) {
            throw new RuntimeException("No hay boletos vendidos para esta rifa");
        }

        // Construir lista de strings representando los números vendidos (por ejemplo "0001", "A_23"... según tu formato)
        List<String> allNumbers = vendidos.stream()
                .map(NumberRaffle::getNumber)
                .map(n -> n.contains("_") ? n.split("_")[1] : n)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        // 3️⃣ Genera semilla segura y hash de integridad (semilla + raffleId + lista de números)
        SecureRandom sr = new SecureRandom();
        byte[] seedBytes = new byte[24];
        sr.nextBytes(seedBytes);
        String semilla = "RNG-" + Base64.getUrlEncoder().withoutPadding().encodeToString(seedBytes);

        StringBuilder sb = new StringBuilder();
        sb.append(semilla).append("|").append(raffleId).append("|");
        // concatenamos todos los números con separador (determinístico)
        for (String n : allNumbers) {
            sb.append(n).append(",");
        }
        String contenidoHash = sb.toString();

        // calcular SHA-256
        String hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(contenidoHash.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            hash = "SHA256-" + Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando hash", e);
        }

        // 4️⃣ Selecciona ganadores de forma determinista usando la semilla (mezcla determinística)
        // -> Si queremos reproducibilidad: hacemos un shuffle basado en SecureRandom con semilla derivada.
        // Derivamos un número largo de la semilla para inicializar Random
        long derivedSeed = 0L;
        byte[] seedForLong = java.util.Arrays.copyOf(seedBytes, 8);
        for (int i = 0; i < seedForLong.length; i++) {
            derivedSeed = (derivedSeed << 8) | (seedForLong[i] & 0xff);
        }
        java.util.Random deterministicRandom = new java.util.Random(derivedSeed);

        List<NumberRaffle> copyVendidos = new java.util.ArrayList<>(vendidos);
        // Fisher-Yates shuffle usando deterministicRandom
        for (int i = copyVendidos.size() - 1; i > 0; i--) {
            int j = deterministicRandom.nextInt(i + 1);
            NumberRaffle tmp = copyVendidos.get(i);
            copyVendidos.set(i, copyVendidos.get(j));
            copyVendidos.set(j, tmp);
        }

        List<NumberRaffle> seleccionados = copyVendidos.stream()
                .limit(Math.max(1, numeroGanadores))
                .collect(Collectors.toList());

        // 5️⃣ Mapea ganadores a DTO
        List<WinnerDTO> ganadores = seleccionados.stream()
                .map(n -> {
                    String numero = n.getNumber().contains("_") ? n.getNumber().split("_")[1] : n.getNumber();
                    Buyer b = n.getBuyer();
                    return new WinnerDTO(
                            numero,
                            b != null ? b.getName() : "",
                            b != null ? b.getApellido() : "",
                            b != null ? b.getEmail() : "",
                            b != null ? b.getPhone() : ""
                    );
                })
                .collect(Collectors.toList());

        // 6️⃣ Convierte los DTO a objetos embebidos para guardar en la BD
        List<WinnerEmbeddable> ganadoresEmbeddable = ganadores.stream()
                .map(w -> new WinnerEmbeddable(
                        w.getNumero(),
                        w.getNombre(),
                        w.getApellido(),
                        w.getEmail(),
                        w.getTelefono()
                ))
                .collect(Collectors.toList());

        // 7️⃣ Guarda acta con semilla y hash
        SorteoActa acta = SorteoActa.builder()
                .raffleId(raffleId)
                .fechaEjecucion(LocalDateTime.now())
                .semilla(semilla)
                .hashGenerado(hash)
                .ganadores(ganadoresEmbeddable)
                .build();

        SorteoActa actaGuardada = sorteoActaRepository.save(acta);

        // 8️⃣ Notificar ganadores (si aplica)
        for (WinnerDTO g : ganadores) {
            if (g.getEmail() != null && !g.getEmail().isBlank()) {
                emailService.sendWinnerNotificationEmail(
                        g.getEmail(),
                        g.getNombre(),
                        g.getApellido(),
                        g.getNumero(),
                        "Rifa #" + raffleId,
                        actaGuardada.getFechaEjecucion(),
                        actaGuardada.getSemilla(),
                        actaGuardada.getHashGenerado()
                );
            }
        }

        // 9️⃣ Retorna DTO con toda la información para el front (incluye allNumbers)
        return new ResultadoSorteoDTO(
                ganadores,
                actaGuardada.getId(),
                semilla,
                hash,
                allNumbers
        );
    }







}
