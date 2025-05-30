package com.aba.raffle.proyecto.model.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Buyer {

    private String name;
    private String apellido;
    private String pais;
    private String email;
    private String prefix;
    private String phone;

}
