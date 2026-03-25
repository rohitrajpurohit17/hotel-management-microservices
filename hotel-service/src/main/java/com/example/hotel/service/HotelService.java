package com.example.hotel.service;

import com.example.hotel.domain.Hotel;
import com.example.hotel.domain.Room;
import com.example.hotel.dto.CreateHotelRequest;
import com.example.hotel.dto.CreateRoomRequest;
import com.example.hotel.dto.HotelResponse;
import com.example.hotel.dto.RoomResponse;
import com.example.hotel.repository.HotelRepository;
import com.example.hotel.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    public HotelService(HotelRepository hotelRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
    }

    public HotelResponse createHotel(CreateHotelRequest request) {
        Hotel hotel = hotelRepository.save(new Hotel(
                request.name(),
                request.city(),
                request.address(),
                request.rating()
        ));
        return toHotelResponse(hotel);
    }

    @Transactional(readOnly = true)
    public List<HotelResponse> findAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::toHotelResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public HotelResponse findHotel(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .map(this::toHotelResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
    }

    public RoomResponse addRoom(Long hotelId, CreateRoomRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        Room room = new Room(
                request.roomNumber(),
                request.type(),
                request.pricePerNight(),
                request.totalUnits()
        );
        hotel.addRoom(room);
        hotelRepository.save(hotel);
        return toRoomResponse(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> findRoomsByHotelId(Long hotelId) {
        return roomRepository.findAllByHotelId(hotelId).stream()
                .map(this::toRoomResponse)
                .toList();
    }

    private HotelResponse toHotelResponse(Hotel hotel) {
        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getCity(),
                hotel.getAddress(),
                hotel.getRating(),
                hotel.getRooms().stream().map(this::toRoomResponse).toList()
        );
    }

    private RoomResponse toRoomResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getHotel().getId(),
                room.getRoomNumber(),
                room.getType(),
                room.getPricePerNight(),
                room.getTotalUnits()
        );
    }
}

