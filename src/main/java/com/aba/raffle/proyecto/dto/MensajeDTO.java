package com.aba.raffle.proyecto.dto;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}
