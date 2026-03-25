package com.example.inventory.dto;

public record InventoryResponse(
        Long id,
        Long roomId,
        Integer availableUnits,
        Integer reservedUnits
) {
}

