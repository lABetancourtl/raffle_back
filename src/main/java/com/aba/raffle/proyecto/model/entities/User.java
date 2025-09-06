package com.aba.raffle.proyecto.model.entities;

import com.aba.raffle.proyecto.model.enums.EstadoUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuariosHome")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    //Datos iniciales para el registro
    private String email;
    private String password;
    private String urlImagDocFront;
    private String urlImagDocBack;

    //Datos obtenidos del documento de identidad
    private String name;
    private String surName;
    private String DocNumber;
    private String dateOfBirth;

    //Estado de la cuenta
    private EstadoUser estadoUser;



}
