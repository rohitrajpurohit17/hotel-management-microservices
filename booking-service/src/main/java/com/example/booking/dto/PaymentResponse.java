package com.example.booking.dto;

public record PaymentResponse(
        boolean success,
        String message,
        String transactionId,
        String status
) {
}

