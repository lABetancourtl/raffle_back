package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.dto.PagoRequestDTO;
import com.aba.raffle.proyecto.model.entities.NumberRaffle;
import com.aba.raffle.proyecto.model.entities.PaymentOperation;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.repositories.NumberRepository;
import com.aba.raffle.proyecto.repositories.PaymentOperationRepository;
import com.aba.raffle.proyecto.services.MercadoPagoService;
import com.aba.raffle.proyecto.services.PurchaseService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class MercadoPagoServiceImpl implements MercadoPagoService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    private final NumberRepository  numberRepository;
    private final PurchaseService purchaseService;
    private PaymentOperationRepository paymentOperationRepository;

    @Value("${app.base.url}")
    private String baseUrl;


    // Inyección por constructor
    public MercadoPagoServiceImpl(NumberRepository numberRepository, PurchaseService purchaseService, PaymentOperationRepository paymentOperationRepository) {
        this.numberRepository = numberRepository;
        this.purchaseService = purchaseService;
        this.paymentOperationRepository = paymentOperationRepository;
    }

    public Map<String, String> crearPreferenciaPago(String descripcion, int cantidad, double precio, String email, String externalReference) throws Exception {
        MercadoPagoConfig.setAccessToken(accessToken);

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .title(descripcion)
                .quantity(cantidad)
                .unitPrice(BigDecimal.valueOf(precio))
                .currencyId("COP")

                .build();

        PreferencePayerRequest payer = PreferencePayerRequest.builder()
                .email(email)
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(item))
                .payer(payer)
                .externalReference(externalReference)
                .notificationUrl(baseUrl + "/api/mercadopago/webhook")
                .backUrls(PreferenceBackUrlsRequest.builder()
                        .success("https://raffle-back-2.onrender.com/home")
                        .failure("https://raffle-back-2.onrender.com/home")
                        .pending("https://raffle-back-2.onrender.com/home")
                        .build())
                .autoReturn("approved")
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return Map.of(
                "preferenceId", preference.getId(),
                "init_point", preference.getSandboxInitPoint()
        );
    }

    public void procesarPago(Map<String, Object> payload) {
        Object id = payload.get("data") instanceof Map ?
                ((Map<?, ?>) payload.get("data")).get("id") : null;

        if (id != null) {
            try {
                MercadoPagoConfig.setAccessToken(accessToken);
                PaymentClient paymentClient = new PaymentClient();
                Payment payment = paymentClient.get(Long.parseLong(id.toString()));

                // Datos del pago
                String status = payment.getStatus();
                String email = payment.getPayer().getEmail();
                double monto = payment.getTransactionAmount().doubleValue();
                String metodo = payment.getPaymentMethodId();
                String moneda = payment.getCurrencyId();
                String externalReference = payment.getExternalReference();
                LocalDateTime fecha = payment.getDateApproved() != null
                        ? payment.getDateApproved().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                        : LocalDateTime.now();

                // Buscar números asociados
                List<NumberRaffle> numeros = numberRepository.findByPaymentSessionId(externalReference);

                // Preparar operación
                PaymentOperation op = new PaymentOperation();
                op.setPaymentId(payment.getId().toString());
                op.setStatus(status);
                op.setMonto(monto);
                op.setMoneda(moneda);
                op.setMetodoPago(metodo);
                op.setFechaPago(fecha);
                op.setExternalReference(externalReference);
                op.setCompradorEmail(email);
                op.setRawPayload(payload.toString());
                op.setRegistradoEn(LocalDateTime.now());

                if (numeros.isEmpty()) {
                    System.out.println("❌ No se encontraron números asociados a: " + externalReference);
                    op.setExpirada(true);
                    paymentOperationRepository.save(op);
                    return;
                }

                NumberRaffle primerNumero = numeros.get(0);
                LocalDateTime reservedAt = primerNumero.getReservedAt();

                if (reservedAt == null || reservedAt.isBefore(LocalDateTime.now().minusMinutes(1))) {
                    System.out.println("❌ La reserva expiró. Liberando números.");

                    op.setExpirada(true);
                    op.setRaffleId(primerNumero.getRaffleId().toString());
                    op.setCantidadNumeros(numeros.size());

                    if (primerNumero.getBuyer() != null) {
                        op.setCompradorNombre(primerNumero.getBuyer().getName());
                        op.setCompradorApellido(primerNumero.getBuyer().getApellido());
                        op.setCompradorPais(primerNumero.getBuyer().getPais());
                        op.setCompradorTelefono(primerNumero.getBuyer().getPhone());
                    }

                    liberarNumeros(numeros);
                    paymentOperationRepository.save(op);
                    return;
                }

                // La reserva aún es válida
                op.setExpirada(false);
                op.setRaffleId(primerNumero.getRaffleId().toString());
                op.setCantidadNumeros(numeros.size());
                op.setNumerosComprados(numeros.stream().map(NumberRaffle::getNumber).toList());

                if (primerNumero.getBuyer() != null) {
                    op.setCompradorNombre(primerNumero.getBuyer().getName());
                    op.setCompradorApellido(primerNumero.getBuyer().getApellido());
                    op.setCompradorPais(primerNumero.getBuyer().getPais());
                    op.setCompradorTelefono(primerNumero.getBuyer().getPhone());
                }

                paymentOperationRepository.save(op);

                if ("approved".equalsIgnoreCase(status)) {
                    for (NumberRaffle numero : numeros) {
                        numero.setStateNumber(EstadoNumber.VENDIDO);
                        numero.setReservedAt(LocalDateTime.now());
                    }
                    numberRepository.saveAll(numeros);
                } else {
                    liberarNumeros(numeros);
                }

            } catch (Exception e) {
                System.err.println("❌ Error al procesar el pago: " + e.getMessage());
            }
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
        System.out.println("🔁 Números liberados correctamente por pago no aprobado o expiración.");
    }


    @Override
    public Map<String, String> iniciarProcesoDePago(PagoRequestDTO datosPago) throws Exception {
        String externalReference = UUID.randomUUID().toString();
        BuyRequestDTO buyRequestDTO = datosPago.buyer();

        // 1. Reservar los números
        purchaseService.comprarNumero(buyRequestDTO, externalReference);

        // 2. Crear la preferencia de pago
        return crearPreferenciaPago(
                datosPago.descripcion(),
                datosPago.cantidad(),
                datosPago.precio(),
                datosPago.email(),
                externalReference
        );
    }

    @Scheduled(fixedRate = 60000) // cada 1 minuto
    public void liberarReservasVencidas() {
        LocalDateTime hace10Min = LocalDateTime.now().minusMinutes(1);
        List<NumberRaffle> vencidos = numberRepository.findByStateNumberAndReservedAtBefore(
                EstadoNumber.RESERVADO, hace10Min
        );

        for (NumberRaffle numero : vencidos) {
            numero.setStateNumber(EstadoNumber.DISPONIBLE);
            numero.setBuyer(null);
            numero.setReservedAt(null);
            numero.setPaymentSessionId(null);
        }

        if (!vencidos.isEmpty()) {
            numberRepository.saveAll(vencidos);
            System.out.println("⏰ Reservas expiradas liberadas: " + vencidos.size());
        }
    }



}
