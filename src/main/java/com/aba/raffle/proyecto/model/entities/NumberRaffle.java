package com.aba.raffle.proyecto.model.entities;

import com.aba.raffle.proyecto.model.enums.EstadoNumber;

import com.aba.raffle.proyecto.model.vo.Buyer;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "numbers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NumberRaffle {

    @Id
    @EqualsAndHashCode.Include
    private String number; // puedes mantenerlo como String si es el número de rifa

    @Enumerated(EnumType.STRING)
    private EstadoNumber stateNumber;

    @Embedded
    private Buyer buyer; // buyer debe estar anotado con @Embeddable

    private Long raffleId; // relación simple, o @ManyToOne a otra entidad Raffle

    private LocalDateTime reservedAt;

    private String paymentSessionId;
}
