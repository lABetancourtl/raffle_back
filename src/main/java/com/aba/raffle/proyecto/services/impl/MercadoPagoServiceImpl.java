package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.dto.BuyRequestDTO;
import com.aba.raffle.proyecto.dto.PagoRequestDTO;
import com.aba.raffle.proyecto.model.documents.NumberRaffle;
import com.aba.raffle.proyecto.model.documents.PaymentOperation;
import com.aba.raffle.proyecto.model.enums.EstadoNumber;
import com.aba.raffle.proyecto.model.vo.Buyer;
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
    private PaymentOperationRepository paymentOperationRepository;

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
                .notificationUrl("https://3dc8-152-202-206-54.ngrok-free.app/api/mercadopago/webhook")
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
        Object id = payload.get("data") instanceof Map ?
                ((Map<?, ?>) payload.get("data")).get("id") : null;

        if (id != null) {
            try {
                MercadoPagoConfig.setAccessToken(accessToken);
                PaymentClient paymentClient = new PaymentClient();
                Payment payment = paymentClient.get(Long.parseLong(id.toString()));

                // Extraer datos
                String status = payment.getStatus();
                String email = payment.getPayer().getEmail();
                double monto = payment.getTransactionAmount().doubleValue();
                String metodo = payment.getPaymentMethodId();
                String moneda = payment.getCurrencyId();
                String externalReference = payment.getExternalReference();
                LocalDateTime fecha = payment.getDateApproved() != null
                        ? payment.getDateApproved().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                        : LocalDateTime.now();

                // Buscar números reservados por externalReference
                List<NumberRaffle> numeros = numberRepository.findByPaymentSessionId(externalReference);

                // Guardar operación
                PaymentOperation op = new PaymentOperation();
                op.setPaymentId(payment.getId().toString());
                op.setStatus(status);
                op.setMonto(monto);
                op.setMoneda(moneda);
                op.setMetodoPago(metodo);
                op.setFechaPago(fecha);
                op.setExternalReference(externalReference);
                op.setCompradorEmail(email);

                if (!numeros.isEmpty()) {
                    NumberRaffle n = numeros.get(0);
                    op.setRaffleId(n.getRaffleId().toString());
                    op.setCantidadNumeros(numeros.size());
                    op.setNumerosComprados(numeros.stream().map(NumberRaffle::getNumber).toList());

                    // Intentar extraer más datos del comprador (si estaban guardados)
                    op.setCompradorNombre(n.getBuyer() != null ? n.getBuyer().getName() : null);
                    op.setCompradorApellido(n.getBuyer() != null ? n.getBuyer().getApellido() : null);
                    op.setCompradorPais(n.getBuyer() != null ? n.getBuyer().getPais() : null);
                    op.setCompradorTelefono(n.getBuyer() != null ? n.getBuyer().getPhone() : null);
                }

                op.setRawPayload(payload.toString());
                op.setRegistradoEn(LocalDateTime.now());

                paymentOperationRepository.save(op);

                // Actualizar estado si es aprobado
                if ("approved".equalsIgnoreCase(status)) {
                    for (NumberRaffle numero : numeros) {
                        numero.setStateNumber(EstadoNumber.VENDIDO);
                        numero.setReservedAt(LocalDateTime.now());
                    }
                    numberRepository.saveAll(numeros);
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
