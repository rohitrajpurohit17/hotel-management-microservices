package com.example.hotel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateRoomRequest(
        @NotBlank String roomNumber,
        @NotBlank String type,
        @NotNull @DecimalMin("0.0") BigDecimal pricePerNight,
        @NotNull @Min(1) Integer totalUnits
) {
}

