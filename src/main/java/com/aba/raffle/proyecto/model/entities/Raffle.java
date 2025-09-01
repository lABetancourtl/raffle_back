package com.aba.raffle.proyecto.model.entities;



import com.aba.raffle.proyecto.model.enums.EstadoRaffle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "rifas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Raffle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nameRaffle;
    private String description;
    private String startDate;       // podrías usar LocalDate si quieres
    private String endDate;         // podrías usar LocalDate si quieres
    private String urlImagen;

    @Enumerated(EnumType.STRING)
    private EstadoRaffle stateRaffle;

    private BigDecimal priceNumber;
    private int digitLength;
    private int minPurchase;

    @ElementCollection
    @CollectionTable(name = "paquetes", joinColumns = @JoinColumn(name = "raffle_id"))
    @Column(name = "paquete")
    private List<Integer> paquetes;

    @Transient
    private int porcentajeVendidos;

    @Transient
    private int cantidadDisponibles;
}