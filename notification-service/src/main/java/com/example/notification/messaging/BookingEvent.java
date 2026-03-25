package com.example.notification.messaging;

import java.time.OffsetDateTime;

public record BookingEvent(
        String bookingReference,
        Long userId,
        Long roomId,
        String status,
        String userEmail,
        String userPhone,
        String message,
        OffsetDateTime createdAt
) {
}

