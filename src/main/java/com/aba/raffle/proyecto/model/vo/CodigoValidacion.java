package com.aba.raffle.proyecto.model.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CodigoValidacion {

    private String codigo;
    private LocalDateTime fechaCreacion;
}
