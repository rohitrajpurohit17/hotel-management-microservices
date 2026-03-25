package com.example.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank String bookingReference,
        @NotNull @DecimalMin("0.0") BigDecimal amount,
        @NotBlank String currency,
        @NotBlank String paymentMethod,
        @NotBlank String cardNumber
) {
}

