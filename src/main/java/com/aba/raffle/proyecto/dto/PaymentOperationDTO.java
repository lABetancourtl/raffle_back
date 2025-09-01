package com.aba.raffle.proyecto.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PaymentOperationDTO(
        String id,
        String paymentId,
        String status,
        double monto,
        String moneda,
        String metodoPago,
        LocalDateTime fechaPago,
        String compradorNombre,
        String compradorApellido,
        String compradorEmail,
        String raffleId,
        int cantidadNumeros,
        List<NumeroDTO> numerosComprados,
        boolean expirada
) {}

