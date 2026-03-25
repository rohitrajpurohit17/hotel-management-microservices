package com.example.notification.dto;

import java.time.OffsetDateTime;

public record NotificationResponse(
        Long id,
        String eventType,
        String channel,
        String recipient,
        String payload,
        OffsetDateTime createdAt
) {
}

