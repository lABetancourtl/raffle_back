package com.aba.raffle.proyecto.dto;

public record WompiTransaccionDTO(
        Integer amountInCents,
        String currency,
        String email
) {
}
