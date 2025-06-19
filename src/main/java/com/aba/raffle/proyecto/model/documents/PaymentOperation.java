package com.aba.raffle.proyecto.model.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "operaciones_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentOperation {

    @Id
    private String id;

    // Datos del pago desde MercadoPago
    private String paymentId; // ID del pago (de MercadoPago)
    private String status; // approved, rejected, pending, etc.
    private double monto;
    private String moneda;

    private String metodoPago; // Ex: "credit_card", "pix", etc.
    private LocalDateTime fechaPago; // fecha confirmada del pago

    // External reference de la compra (clave para relacionarlo con los números reservados)
    private String externalReference;

    // Datos del comprador
    private String compradorNombre;
    private String compradorApellido;
    private String compradorPais;
    private String compradorEmail;
    private String compradorTelefono;

    // Info de los números de rifa (opcional pero útil para trazabilidad)
    private String raffleId;
    private int cantidadNumeros;
    private List<String> numerosComprados; // solo si quieres guardar los números

    // Datos técnicos / extras
    private String rawPayload; // todo el JSON que llegó por webhook, por si acaso
    private LocalDateTime registradoEn; // cuándo se registró esta operación

}
