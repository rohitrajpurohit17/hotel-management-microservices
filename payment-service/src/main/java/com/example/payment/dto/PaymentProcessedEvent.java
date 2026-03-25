package com.example.payment.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentProcessedEvent(
        String bookingReference,
        String transactionId,
        BigDecimal amount,
        String currency,
        String status,
        OffsetDateTime processedAt
) {
}

