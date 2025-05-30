package com.aba.raffle.proyecto.model.documents;


import com.aba.raffle.proyecto.model.enums.EstadoRaffle;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document("rifas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Raffle {

    @Id
    @EqualsAndHashCode.Include
    private ObjectId id;

    private String nameRaffle;      // nombre de la rifa
    private String description;     // descripcion de la rifa
    private String startdate;       // fecha de inicio de la rifa
    private String endDate;         // fecha de finalizacion de la rifa (finaliza cuando se vendan todos los numeros)
    private EstadoRaffle stateRaffle;     // estado de la rifa (pendiente, en proceso, finalizada)
    private BigDecimal priceNumber; // precio de cada numero
    private int digitLength;        // numero de digitos de cada numero
    private int minPurchase;        // minimo de compra para que pueda venderse

}
