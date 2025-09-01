package com.aba.raffle.proyecto.model.entities;

import com.aba.raffle.proyecto.model.enums.EstadoTransaction;
import com.aba.raffle.proyecto.model.vo.Buyer;
import com.aba.raffle.proyecto.model.vo.PaymentDetails;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transaction_raffle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionRaffle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long raffleId; // o @ManyToOne a Raffle si quieres

    @Embedded
    private Buyer buyer;

    @ElementCollection
    @CollectionTable(name = "transaction_numbers", joinColumns = @JoinColumn(name = "transaction_id"))
    @Column(name = "number")
    private List<String> numbers;

    private int amountPaid;
    private LocalDateTime paymentDate;
    private String paymentGateway;

    @Embedded
    private PaymentDetails paymentDetails;

    @Enumerated(EnumType.STRING)
    private EstadoTransaction estadoTransaccion; // APROBADA, RECHAZADA, PENDIENTE
}