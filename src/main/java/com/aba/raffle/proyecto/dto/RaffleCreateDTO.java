package com.aba.raffle.proyecto.dto;

import com.aba.raffle.proyecto.model.enums.EstadoRaffle;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

public record RaffleCreateDTO(

        @NotBlank
        String urlImagen,

        @NotBlank
        String nameRaffle,

        @NotBlank
        String description,

        @NotNull
        EstadoRaffle stateRaffle,

        @NotNull
        @Min(value = 0, message = "El precio debe ser positivo")
        BigDecimal pricePerNumber,

        @Min(value = 1, message = "La compra mínima debe ser al menos 1")
        int          minPurchase,

        @Min(value = 1, message = "La cantidad de dígitos debe ser al menos 1")
        @Max(value = 5, message = "La cantidad de dígitos debe ser máximo 5")
        int digitLength
) {
}
