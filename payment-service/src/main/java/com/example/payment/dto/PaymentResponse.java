package com.example.payment.dto;

public record PaymentResponse(
        boolean success,
        String message,
        String transactionId,
        String status
) {
}

