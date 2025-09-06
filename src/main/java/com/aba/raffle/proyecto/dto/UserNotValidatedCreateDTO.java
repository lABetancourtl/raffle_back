package com.aba.raffle.proyecto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserNotValidatedCreateDTO(

        //Datos iniciales para el registro
        @NotBlank @Email @Length( max = 100)         String email,
        @NotBlank @Length        ( min = 6, max = 20) String password,
        @NotBlank String urlImagDocFront,
        @NotBlank String urlImagDocBack
) {
}
