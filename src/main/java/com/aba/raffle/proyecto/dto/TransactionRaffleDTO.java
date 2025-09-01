package com.aba.raffle.proyecto.dto;

import com.aba.raffle.proyecto.model.vo.PaymentDetails;

import java.util.List;

public record TransactionRaffleDTO(
        BuyerDTO buyer,
        List<String> numbers,
        int amountPaid,
        String paymentGateway,
        PaymentDetails paymentDetails,
        String estadoTransaccion
) {
}
