package com.aba.raffle.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record BuyRequestDTO(
                                          String raffleId,
    @Min(value = 1, message = "La cantidad debe ser mayor o igual a 1")
                                          int quantity,
    @NotBlank                             String buyerName,
    @NotBlank                             String buyerApellido,
    @NotBlank                             String buyerPais,
    @NotBlank @Email @Length ( max = 100) String buyerEmail,
    @NotBlank @Email @Length ( max = 100) String buyerConfirmarEmail,
    @NotBlank                             String buyerPrefix,
    @NotBlank                             String buyerPhone

) {
}
