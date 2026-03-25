package com.example.booking.domain;

// This is an enum (enumeration) class in Java
// Enums are used to define a fixed set of constants
// They are perfect for representing status values, types, or any predefined set of options
// Unlike regular classes, enums cannot be instantiated with 'new', and they have a fixed number of instances
// In this case, BookingStatus represents the different states a hotel booking can be in during its lifecycle

// The booking status indicates the current state of a booking process
// This helps track whether a booking was successful, failed, or is still in progress
// Different statuses can trigger different actions in the system (e.g., sending notifications, releasing inventory)
public enum BookingStatus {

    // PENDING: The booking has been initiated but not yet fully processed
    // This is the initial status when a booking request is received
    // The system is still validating user, checking inventory, and processing payment
    PENDING,

    // CONFIRMED: The booking has been successfully completed
    // All validations passed, payment was successful, inventory was reserved
    // This is the final successful state of a booking
    CONFIRMED,

    // PAYMENT_FAILED: The payment processing failed during booking
    // This could be due to insufficient funds, card declined, or payment service error
    // The booking cannot proceed without successful payment
    PAYMENT_FAILED,

    // INVENTORY_UNAVAILABLE: The requested room is not available for the dates
    // This happens when another booking already reserved the room, or inventory service reports unavailability
    // The booking cannot be confirmed without available inventory
    INVENTORY_UNAVAILABLE,

    // USER_NOT_FOUND: The user making the booking could not be found or validated
    // This occurs when the user ID doesn't exist in the user service, or user validation fails
    // Bookings cannot be made for non-existent users
    USER_NOT_FOUND,

    // DEGRADED: The booking was created but some services were unavailable during processing
    // This is a fallback status when the system is running in degraded mode
    // The booking might be partially processed, and manual intervention may be needed
    DEGRADED
}
