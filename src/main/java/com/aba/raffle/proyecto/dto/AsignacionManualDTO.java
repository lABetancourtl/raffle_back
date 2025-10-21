package com.aba.raffle.proyecto.dto;

import jakarta.validation.Valid;

public record AsignacionManualDTO(
        @Valid BuyRequestDTO data,
        @Valid NumeroDTO numeroManual
) {}
