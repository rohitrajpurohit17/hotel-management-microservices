package com.example.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long roomId;

    private Integer availableUnits;
    private Integer reservedUnits;

    protected InventoryItem() {
    }

    public InventoryItem(Long roomId, Integer availableUnits) {
        this.roomId = roomId;
        this.availableUnits = availableUnits;
        this.reservedUnits = 0;
    }

    public void replaceAvailableUnits(Integer availableUnits) {
        this.availableUnits = availableUnits;
    }

    public boolean reserve(Integer quantity) {
        if (availableUnits < quantity) {
            return false;
        }
        availableUnits -= quantity;
        reservedUnits += quantity;
        return true;
    }

    public void release(Integer quantity) {
        int releasable = Math.min(quantity, reservedUnits);
        reservedUnits -= releasable;
        availableUnits += releasable;
    }

    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Integer getAvailableUnits() {
        return availableUnits;
    }

    public Integer getReservedUnits() {
        return reservedUnits;
    }
}

