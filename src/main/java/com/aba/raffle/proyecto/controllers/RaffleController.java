package com.aba.raffle.proyecto.controllers;


import com.aba.raffle.proyecto.dto.*;
import com.aba.raffle.proyecto.model.entities.NumberRaffle;
import com.aba.raffle.proyecto.model.entities.Raffle;
import com.aba.raffle.proyecto.model.entities.SorteoActa;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.repositories.SorteoActaRepository;
import com.aba.raffle.proyecto.services.PdfService;
import com.aba.raffle.proyecto.services.RaffleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/raffle")
public class RaffleController {

    private final RaffleService raffleService;


    @PostMapping("/crearRifa")
    public ResponseEntity<MensajeDTO<String>> crearRifa(@Valid @RequestBody RaffleCreateDTO raffleCreate) throws Exception{
        raffleService.crearRifa(raffleCreate);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Rifa creada exitosamente"));
    }

    @GetMapping("/numerosPorEmail")
    public ResponseEntity<List<NumberRaffle>> obtenerNumerosPorEmail(@RequestParam String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email no válido");
        }
        List<NumberRaffle> numeros = raffleService.obtenerNumerosPorEmail(email);
        return ResponseEntity.ok(numeros);
    }

    @GetMapping("/numerosPorEmail/soloNumeros")
    public ResponseEntity<List<NumeroDTO>> obtenerSoloNumerosPorEmail(@RequestParam String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email no válido");
        }
        List<NumeroDTO> numeros = raffleService.obtenerSoloNumerosPorEmail(email);
        System.out.println("Numeros: "+numeros);
        return ResponseEntity.ok(numeros);
    }


    @GetMapping("/clientePorNumero")
    public ResponseEntity<MensajeDTO<ResultadoBuyerDTO>> obtenerClientePorNumero(
            @RequestParam String raffleId,
            @RequestParam String numero) throws Exception {

        if (!numero.matches("^[0-9]{1,4}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número no válido");
        }

        String numeroCompleto = raffleId + "_" + numero;

        ResultadoBuyerDTO resultado = raffleService.obtenerClientePorNumero(numeroCompleto);
        return ResponseEntity.ok(new MensajeDTO<>(false, resultado));
    }


    @PatchMapping("/cambiarEstadoNumero")
    public ResponseEntity<MensajeDTO<String>> cambiarStateNumber(@Valid @RequestBody CambiarStateNumberDTO cambiarStateNumberDTO) throws Exception{
        raffleService.cambiarStateNumber(cambiarStateNumberDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Estado cambiado correctamente"));
    }

    @GetMapping("/numerosPorEstado")
    public ResponseEntity<List<NumberRaffle>> obtenerNumerosPorEstado(@RequestParam String estadoNumber) {
        EstadoNumber estado;
        try {
            estado = EstadoNumber.valueOf(estadoNumber.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido");
        }
        List<NumberRaffle> numeros = raffleService.obtenerNumerosPorEstado(estado);

        return ResponseEntity.ok(numeros);
    }

    @PatchMapping("/cambiarEstadoRifa")
    public ResponseEntity<MensajeDTO<String>> cambiarStateRaffle(@Valid @RequestBody CambiarStateRaffleDTO cambiarStateRaffleDTO) throws Exception{
        raffleService.cambiarStateRaffle(cambiarStateRaffleDTO);

        return ResponseEntity.ok(new MensajeDTO<>(false, "Estado cambiado correctamente"));
    }

    @GetMapping("/allRifas")
    public ResponseEntity<List<Raffle>> obtenerTodasLasRifas() {
        List<Raffle> raffles = raffleService.obtenerTodasLasRifas();
        return ResponseEntity.ok(raffles);
    }

    @GetMapping("/activa")
    public ResponseEntity<Raffle> rifaActiva() {
        Raffle raffle = raffleService.obtenerRifaActiva()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay rifa activa"));
        return ResponseEntity.ok(raffle);
    }

    @GetMapping("/operaciones/{raffleId}")
    public ResponseEntity<List<PaymentOperationDTO>> getOperacionesByRaffle(@PathVariable String raffleId) {
        List<PaymentOperationDTO> operaciones = raffleService.getOperacionesByRaffle(raffleId);
        return ResponseEntity.ok(operaciones);
    }

    @PostMapping("/ejecutarSorteo/{raffleId}")
    public ResponseEntity<MensajeDTO<ResultadoSorteoDTO>> ejecutarSorteo(
            @PathVariable Long raffleId,
            @RequestParam(defaultValue = "1") int numeroGanadores) {

        try {
            ResultadoSorteoDTO resultado = raffleService.ejecutarSorteo(raffleId, numeroGanadores);
            return ResponseEntity.ok(new MensajeDTO<>(false, resultado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MensajeDTO<>(true, null));
        }
    }


    @Autowired
    private PdfService pdfService;
    @Autowired
    private SorteoActaRepository sorteoActaRepository;

    @GetMapping("/acta/{sorteoId}/pdf")
    public ResponseEntity<byte[]> descargarActa(@PathVariable Long sorteoId) throws Exception {
        SorteoActa acta = sorteoActaRepository.findById(sorteoId)
                .orElseThrow(() -> new RuntimeException("Acta no encontrada"));

        // En un caso real deberías recuperar los ganadores asociados
        List<WinnerDTO> ganadores = acta.getNumerosGanadores().stream()
                .map(n -> new WinnerDTO(n, "", "", "", ""))
                .toList();

        byte[] pdf = pdfService.generarActaPdf(acta, ganadores);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=ActaSorteo_" + sorteoId + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }






}
