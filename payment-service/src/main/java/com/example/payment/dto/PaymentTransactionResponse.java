package com.example.payment.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentTransactionResponse(
        Long id,
        String bookingReference,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String transactionId,
        String status,
        OffsetDateTime processedAt
) {
}

