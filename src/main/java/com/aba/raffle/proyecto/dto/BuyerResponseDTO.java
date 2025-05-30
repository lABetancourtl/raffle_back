package com.aba.raffle.proyecto.dto;

import java.math.BigInteger;
import java.util.List;

public record BuyerResponseDTO(
        String raffleName,
        List<Integer> numbersAssigned,
        BigInteger totalPrice
) {
}
