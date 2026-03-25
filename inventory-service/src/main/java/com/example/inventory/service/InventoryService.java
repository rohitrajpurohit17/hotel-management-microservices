package com.example.inventory.service;

import com.example.inventory.domain.InventoryItem;
import com.example.inventory.dto.InventoryActionResponse;
import com.example.inventory.dto.InventoryRequest;
import com.example.inventory.dto.InventoryResponse;
import com.example.inventory.repository.InventoryItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class InventoryService {

    private final InventoryItemRepository repository;

    public InventoryService(InventoryItemRepository repository) {
        this.repository = repository;
    }

    public InventoryResponse createOrUpdate(InventoryRequest request) {
        InventoryItem item = repository.findByRoomId(request.roomId())
                .map(existing -> {
                    existing.replaceAvailableUnits(request.availableUnits());
                    return existing;
                })
                .orElseGet(() -> new InventoryItem(request.roomId(), request.availableUnits()));
        return toResponse(repository.save(item));
    }

    @Transactional(readOnly = true)
    public InventoryResponse findByRoomId(Long roomId) {
        return repository.findByRoomId(roomId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found"));
    }

    public InventoryActionResponse reserveRoom(Long roomId, Integer quantity) {
        InventoryItem item = repository.findByRoomId(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found"));
        boolean reserved = item.reserve(quantity);
        if (!reserved) {
            return new InventoryActionResponse(false, "Inventory unavailable", toResponse(item));
        }
        return new InventoryActionResponse(true, "Inventory reserved", toResponse(item));
    }

    public InventoryActionResponse releaseRoom(Long roomId, Integer quantity) {
        InventoryItem item = repository.findByRoomId(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found"));
        item.release(quantity);
        return new InventoryActionResponse(true, "Inventory released", toResponse(item));
    }

    private InventoryResponse toResponse(InventoryItem item) {
        return new InventoryResponse(
                item.getId(),
                item.getRoomId(),
                item.getAvailableUnits(),
                item.getReservedUnits()
        );
    }
}

