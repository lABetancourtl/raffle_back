package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.dto.PagoRequestDTO;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.vo.Buyer;
import com.aba.raffle.proyecto.repositories.NumberRepository;
import com.aba.raffle.proyecto.services.MercadoPagoService;
import com.aba.raffle.proyecto.services.PurchaseService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    // Inyección por constructor
    public MercadoPagoServiceImpl(NumberRepository numberRepository, PurchaseService purchaseService) {
        this.numberRepository = numberRepository;
        this.purchaseService = purchaseService;
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
                .notificationUrl("https://7f37-152-202-206-54.ngrok-free.app/api/mercadopago/webhook")
                .backUrls(PreferenceBackUrlsRequest.builder()
                        .success("https://tuapp.com/pago-exitoso")
                        .failure("https://tuapp.com/pago-fallido")
                        .pending("https://tuapp.com/pago-pendiente")
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
        System.out.println("📦 Webhook recibido en impl: " + payload);

        Object id = payload.get("data") instanceof Map ?
                ((Map<?, ?>) payload.get("data")).get("id") : null;

        if (id != null) {
            System.out.println("✅ ID de pago recibido: " + id);

            try {
                MercadoPagoConfig.setAccessToken(accessToken);

                PaymentClient paymentClient = new PaymentClient();
                Payment payment = paymentClient.get(Long.parseLong(id.toString()));

                String status = payment.getStatus();
                String email = payment.getPayer().getEmail();
                double monto = payment.getTransactionAmount().doubleValue();
                String externalReference = payment.getExternalReference();

                System.out.println("🧾 Estado del pago: " + status);
                System.out.println("📧 Email comprador: " + email);
                System.out.println("💵 Monto pagado: " + monto);
                System.out.println("🔗 External Reference: " + externalReference);

                if ("approved".equalsIgnoreCase(status)) {
                    System.out.println("✅ El pago fue aprobado. Actualizando números...");

                    List<NumberRaffle> numerosReservados = numberRepository.findByPaymentSessionId(externalReference);

                    for (NumberRaffle numero : numerosReservados) {
                        numero.setStateNumber(EstadoNumber.VENDIDO);
                        numero.setReservedAt(LocalDateTime.now());
                    }

                    numberRepository.saveAll(numerosReservados);
                    System.out.println("🎉 Números actualizados a VENDIDO");
                }

            } catch (Exception e) {
                System.err.println("❌ Error al procesar el pago: " + e.getMessage());
            }
        }
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



}
