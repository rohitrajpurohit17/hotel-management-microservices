package com.example.booking.dto;

public record InventoryActionResponse(
        boolean success,
        String message,
        InventorySummary inventory
) {
}

