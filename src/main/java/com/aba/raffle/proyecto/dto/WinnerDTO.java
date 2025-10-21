package com.aba.raffle.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WinnerDTO {
    private String numero;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
