package com.aba.raffle.proyecto.model.documents;

import com.aba.raffle.proyecto.model.enums.EstadoTransaction;
import com.aba.raffle.proyecto.model.vo.Buyer;
import com.aba.raffle.proyecto.model.vo.PaymentDetails;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document("transactionRaffle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
//se urara cuando un pago sea exitoso para almacenar los datos de la transaction
public class TransactionRaffle {

    @Id
    private ObjectId id;

    private String raffleId;
    private Buyer buyer;
    private List<String> numbers;
    private int amountPaid;
    private LocalDateTime paymentDate;
    private String paymentGateway; // Ej: "Wompi", "PayU", etc.
    private PaymentDetails paymentDetails;
    private EstadoTransaction estadoTransaccion; //     APROBADA, RECHAZADA, PENDIENTE
}
