package com.aba.raffle.proyecto.model.entities;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "operaciones_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaymentOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos del pago desde MercadoPago
    private String paymentId;
    private String status;
    private double monto;
    private String moneda;
    private String metodoPago;
    private LocalDateTime fechaPago;

    private String externalReference;

    // Datos del comprador
    private String compradorNombre;
    private String compradorApellido;
    private String compradorPais;
    private String compradorEmail;
    private String compradorTelefono;

    // Info de los números de rifa
    private String raffleId;
    private int cantidadNumeros;

    @ElementCollection
    @CollectionTable(name = "numeros_comprados", joinColumns = @JoinColumn(name = "payment_operation_id"))
    @Column(name = "numero")
    private List<String> numerosComprados;

    // Datos técnicos / extras
    @Column(columnDefinition = "TEXT")
    private String rawPayload;

    private LocalDateTime registradoEn;

    private boolean expirada;
}