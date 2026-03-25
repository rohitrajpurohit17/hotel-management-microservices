package com.example.inventory.dto;

public record InventoryActionResponse(
        boolean success,
        String message,
        InventoryResponse inventory
) {
}

