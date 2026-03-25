package com.example.inventory.web;

import com.example.inventory.dto.InventoryActionResponse;
import com.example.inventory.dto.InventoryAdjustmentRequest;
import com.example.inventory.dto.InventoryRequest;
import com.example.inventory.dto.InventoryResponse;
import com.example.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createOrUpdate(@Valid @RequestBody InventoryRequest request) {
        return inventoryService.createOrUpdate(request);
    }

    @GetMapping("/rooms/{roomId}")
    public InventoryResponse getInventory(@PathVariable Long roomId) {
        return inventoryService.findByRoomId(roomId);
    }

    @PostMapping("/rooms/{roomId}/reserve")
    public InventoryActionResponse reserveInventory(@PathVariable Long roomId,
                                                    @Valid @RequestBody InventoryAdjustmentRequest request) {
        return inventoryService.reserveRoom(roomId, request.quantity());
    }

    @PostMapping("/rooms/{roomId}/release")
    public InventoryActionResponse releaseInventory(@PathVariable Long roomId,
                                                    @Valid @RequestBody InventoryAdjustmentRequest request) {
        return inventoryService.releaseRoom(roomId, request.quantity());
    }
}
