package com.example.booking.dto;

// Importing Java time classes
// OffsetDateTime represents a date and time with timezone offset
// Used for timestamps that need to account for different timezones
import java.time.OffsetDateTime;

// This is a Java Record representing the response sent back to clients after a booking operation
// Records are immutable data carriers, perfect for API responses
// This DTO contains the essential information about a booking that clients need to know
// It includes both success and error information through status and message fields
// Unlike BookingRequest (input), this is the output format for booking APIs
public record BookingResponse(

        // The unique database ID of the created booking
        // Null if booking creation failed
        Long id,

        // The booking reference number (confirmation code)
        // A human-readable identifier for the booking
        // Null if booking failed
        String bookingReference,

        // The status of the booking operation
        // Examples: "CONFIRMED", "PAYMENT_FAILED", "INVENTORY_UNAVAILABLE"
        // Indicates whether the booking was successful or why it failed
        String status,

        // A human-readable message explaining the result
        // Examples: "Booking confirmed successfully", "Payment was declined"
        // Provides details about what happened during the booking process
        String message,

        // The payment transaction ID from the payment service
        // Used for tracking and reconciliation
        // Null if payment processing failed or wasn't attempted
        String transactionId,

        // The ID of the user who made the booking
        // Useful for client-side validation and display
        Long userId,

        // The ID of the room that was booked
        // Helps clients know which room was reserved
        Long roomId,

        // Timestamp when the booking was created
        // Includes timezone information for global applications
        // Null if booking creation failed
        OffsetDateTime createdAt

        // Records automatically generate:
        // - Constructor: BookingResponse(Long, String, String, String, String, Long, Long, OffsetDateTime)
        // - Getters: id(), bookingReference(), status(), etc.
        // - equals(), hashCode(), toString() methods
) {
    // Empty body - using all default generated methods
    // In a real application, you might add custom methods here if needed
}
