package com.aba.raffle.proyecto.model.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Buyer {

    private String name;
    private String apellido;
    private String pais;
    private String email;
    private String prefix;
    private String phone;

}
