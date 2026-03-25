package com.example.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryRequest(
        @NotNull Long roomId,
        @NotNull @Min(0) Integer availableUnits
) {
}

