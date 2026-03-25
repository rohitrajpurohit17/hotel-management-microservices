package com.example.booking.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String bookingReference,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String cardNumber
) {
}

