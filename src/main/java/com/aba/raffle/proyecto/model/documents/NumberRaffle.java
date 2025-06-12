package com.aba.raffle.proyecto.model.documents;

import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.vo.Buyer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("numbers")
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
    private String number;

    private EstadoNumber stateNumber;
    private Buyer buyer; //subdocumento del usuario que compra el numero
    private ObjectId raffleId; //id de la rifa a la que pertenece el numero

    private LocalDateTime reservedAt; // <- CUÁNDO se reservó el número
    private String paymentSessionId;  // <- A QUÉ TRANSACCIÓN está asociado

}
