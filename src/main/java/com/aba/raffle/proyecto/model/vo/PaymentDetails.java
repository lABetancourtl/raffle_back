package com.aba.raffle.proyecto.model.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PaymentDetails {
    private String transactionId;
    private String status;        // Ej: "APPROVED"
    private String method;        // Ej: "PSE", "CARD", "NEQUI"
    private String reference;     // Referencia única de la pasarela
    private String currency;      // Ej: "COP"
}
