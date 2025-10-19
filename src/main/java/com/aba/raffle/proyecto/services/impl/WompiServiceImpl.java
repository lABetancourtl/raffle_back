package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.dto.PagoRequestDTO;
import com.aba.raffle.proyecto.model.entities.NumberRaffle;
import com.aba.raffle.proyecto.model.entities.PaymentOperation;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.repositories.NumberRepository;
import com.aba.raffle.proyecto.repositories.PaymentOperationRepository;
import com.aba.raffle.proyecto.services.PurchaseService;
import com.aba.raffle.proyecto.services.WompiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WompiServiceImpl implements WompiService {

    private final PurchaseService purchaseService;
    private final NumberRepository numberRepository;
    private final PaymentOperationRepository paymentOperationRepository;
    private final EmailService emailService;

    @Value("${wompi.public.key}")
    private String publicKey;

    @Value("${wompi.integrity.secret}")
    private String integritySecret;

    public WompiServiceImpl(PurchaseService purchaseService,
                            NumberRepository numberRepository,
                            PaymentOperationRepository paymentOperationRepository,
                            EmailService emailService) {
        this.purchaseService = purchaseService;
        this.numberRepository = numberRepository;
        this.paymentOperationRepository = paymentOperationRepository;
        this.emailService = emailService;
    }

    @Override
    public Map<String, String> iniciarProcesoDePagoConWompi(PagoRequestDTO datosPago) throws Exception {
        String reference = UUID.randomUUID().toString();
        BuyRequestDTO buyRequestDTO = datosPago.buyer();

        // 1. Reservar los números
        purchaseService.comprarNumero(buyRequestDTO, reference);

        // 2. Calcular el monto total en centavos
        int amountInCents = (int) (datosPago.precio() * 100);

        // 3. Construir cadena para la firma de integridad
        String data = reference + amountInCents + "COP" + integritySecret;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        String signatureIntegrity = hexString.toString();

        // 4. Retornar datos al frontend para armar el botón de Wompi
        Map<String, String> response = new HashMap<>();
        response.put("publicKey", publicKey);
        response.put("currency", "COP");
        response.put("amountInCents", String.valueOf(amountInCents));
        response.put("reference", reference);
        response.put("signatureIntegrity", signatureIntegrity);
        response.put("customerEmail", datosPago.email());

        return response;
    }

    /**
     * Procesar el webhook enviado por Wompi
     */
    public void procesarPago(Map<String, Object> payload) {
        try {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> transaction = (Map<String, Object>) data.get("transaction");

            String reference = (String) transaction.get("reference");
            String status = (String) transaction.get("status");
            String currency = (String) transaction.get("currency");
            Integer amountInCents = (Integer) transaction.get("amount_in_cents");
            double monto = amountInCents / 100.0;

            String metodo = (String) transaction.get("payment_method_type");
            LocalDateTime fecha = LocalDateTime.now();

            // Buscar números reservados
            List<NumberRaffle> numeros = numberRepository.findByPaymentSessionId(reference);

            PaymentOperation op = new PaymentOperation();
            op.setPaymentId(transaction.get("id").toString());
            op.setStatus(status);
            op.setMonto(monto);
            op.setMoneda(currency);
            op.setMetodoPago(metodo);
            op.setFechaPago(fecha);
            op.setExternalReference(reference);
            op.setRawPayload(payload.toString());
            op.setRegistradoEn(LocalDateTime.now());

            if (numeros.isEmpty()) {
                op.setExpirada(true);
                paymentOperationRepository.save(op);
                return;
            }

            NumberRaffle primerNumero = numeros.get(0);
            LocalDateTime reservedAt = primerNumero.getReservedAt();

            if (reservedAt == null || reservedAt.isBefore(LocalDateTime.now().minusMinutes(10))) {
                // Expiró la reserva
                op.setExpirada(true);
                liberarNumeros(numeros);
                paymentOperationRepository.save(op);
                return;
            }

            op.setExpirada(false);
            op.setRaffleId(primerNumero.getRaffleId().toString());
            op.setCantidadNumeros(numeros.size());
            op.setNumerosComprados(numeros.stream().map(NumberRaffle::getNumber).toList());

            if ("APPROVED".equalsIgnoreCase(status)) {
                for (NumberRaffle numero : numeros) {
                    numero.setStateNumber(EstadoNumber.VENDIDO);
                    numero.setReservedAt(LocalDateTime.now());
                }
                numberRepository.saveAll(numeros);

                // Enviar correo al comprador
                if (primerNumero.getBuyer() != null) {
                    emailService.sendPurchaseConfirmationEmail(
                            primerNumero.getBuyer().getEmail(),
                            primerNumero.getBuyer().getName(),
                            monto,
                            currency,
                            metodo,
                            fecha,
                            numeros.stream().map(NumberRaffle::getNumber).toList()
                    );
                }
            } else {
                liberarNumeros(numeros);
            }

            paymentOperationRepository.save(op);

        } catch (Exception e) {
            System.err.println("Error al procesar pago de Wompi: " + e.getMessage());
        }
    }

    private void liberarNumeros(List<NumberRaffle> numeros) {
        for (NumberRaffle numero : numeros) {
            numero.setStateNumber(EstadoNumber.DISPONIBLE);
            numero.setBuyer(null);
            numero.setReservedAt(null);
            numero.setPaymentSessionId(null);
        }
        numberRepository.saveAll(numeros);
    }
}
