package com.aba.raffle.proyecto.model.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class WinnerEmbeddable {
    private String numero;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
