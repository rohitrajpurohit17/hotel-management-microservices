package com.example.hotel.dto;

import java.util.List;

public record HotelResponse(
        Long id,
        String name,
        String city,
        String address,
        Integer rating,
        List<RoomResponse> rooms
) {
}

