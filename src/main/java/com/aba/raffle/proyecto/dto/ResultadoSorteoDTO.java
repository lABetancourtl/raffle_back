package com.aba.raffle.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoSorteoDTO {
    private List<WinnerDTO> ganadores; // Lista completa de ganadores
    private Long actaId;               // ID del acta en BD
    private String semilla;            // Semilla usada para el sorteo
    private String hash;               // Hash generado (para verificación)
}
