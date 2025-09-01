package com.aba.raffle.proyecto.dto;

public record PagoRequestDTO(
        String descripcion,
        int cantidad,
        double precio,
        String email,
        BuyRequestDTO buyer
) {
}
