package com.aba.raffle.proyecto.services.impl;

import com.aba.raffle.proyecto.services.MercadoPagoService;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MercadoPagoServiceImpl implements MercadoPagoService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    public MercadoPagoServiceImpl() {
        // No se puede inicializar aquí porque `@Value` aún no ha sido procesado
    }

    public String crearPreferenciaPago(String descripcion, int cantidad, double precio, String email) throws Exception {
        // Inicializar MercadoPagoConfig con el token inyectado
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
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getId();
    }
}
