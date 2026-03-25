package com.example.hotel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateHotelRequest(
        @NotBlank String name,
        @NotBlank String city,
        @NotBlank String address,
        @Min(1) @Max(5) Integer rating
) {
}

