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
public class PaymentDetails {

    private String transactionId;
    private String status;        // Ej: "APPROVED"
    private String method;        // Ej: "PSE", "CARD", "NEQUI"
    private String reference;     // Referencia única de la pasarela
    private String currency;      // Ej: "COP"
}
