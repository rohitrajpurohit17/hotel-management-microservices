package com.example.booking.client;

import com.example.booking.dto.InventoryActionResponse;
import com.example.booking.dto.InventoryAdjustmentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

    @PostMapping("/api/inventory/rooms/{roomId}/reserve")
    InventoryActionResponse reserveInventory(@PathVariable Long roomId,
                                             @RequestBody InventoryAdjustmentRequest request);

    @PostMapping("/api/inventory/rooms/{roomId}/release")
    InventoryActionResponse releaseInventory(@PathVariable Long roomId,
                                             @RequestBody InventoryAdjustmentRequest request);
}

