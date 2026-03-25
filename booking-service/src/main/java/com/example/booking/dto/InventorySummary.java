package com.example.booking.dto;

public record InventorySummary(
        Long id,
        Long roomId,
        Integer availableUnits,
        Integer reservedUnits
) {
}

