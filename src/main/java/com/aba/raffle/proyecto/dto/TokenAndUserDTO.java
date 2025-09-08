package com.aba.raffle.proyecto.dto;

public record TokenAndUserDTO(
        String token,
        String refreshToken,
        String name,
        String surName,
        String email
) {
}
