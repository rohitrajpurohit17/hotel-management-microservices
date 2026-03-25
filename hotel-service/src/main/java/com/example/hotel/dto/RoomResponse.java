package com.example.hotel.dto;

import java.math.BigDecimal;

public record RoomResponse(
        Long id,
        Long hotelId,
        String roomNumber,
        String type,
        BigDecimal pricePerNight,
        Integer totalUnits
) {
}

