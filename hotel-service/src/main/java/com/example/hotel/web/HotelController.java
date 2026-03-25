package com.example.hotel.web;

import com.example.hotel.dto.CreateHotelRequest;
import com.example.hotel.dto.CreateRoomRequest;
import com.example.hotel.dto.HotelResponse;
import com.example.hotel.dto.RoomResponse;
import com.example.hotel.service.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping("/hotels")
    @ResponseStatus(HttpStatus.CREATED)
    public HotelResponse createHotel(@Valid @RequestBody CreateHotelRequest request) {
        return hotelService.createHotel(request);
    }

    @GetMapping("/hotels")
    public List<HotelResponse> getHotels() {
        return hotelService.findAllHotels();
    }

    @GetMapping("/hotels/{hotelId}")
    public HotelResponse getHotel(@PathVariable Long hotelId) {
        return hotelService.findHotel(hotelId);
    }

    @PostMapping("/hotels/{hotelId}/rooms")
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse addRoom(@PathVariable Long hotelId, @Valid @RequestBody CreateRoomRequest request) {
        return hotelService.addRoom(hotelId, request);
    }

    @GetMapping("/hotels/{hotelId}/rooms")
    public List<RoomResponse> getRooms(@PathVariable Long hotelId) {
        return hotelService.findRoomsByHotelId(hotelId);
    }
}
