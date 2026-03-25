package com.example.notification.messaging;

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

