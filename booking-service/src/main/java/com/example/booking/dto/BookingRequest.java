package com.example.booking.dto;

// Importing validation annotations from Jakarta Bean Validation
// These annotations are used to validate input data automatically
// @NotNull ensures the field is not null
// @NotBlank ensures the string field is not null, empty, or only whitespace
// @DecimalMin ensures the number is at least the specified minimum value
// @Future ensures the date is in the future
// Validation happens automatically when this DTO is used in controller methods

// Importing Java classes for data types
// BigDecimal for precise decimal calculations (money)
// LocalDate for date-only values
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

// This is a Java Record class (introduced in Java 14 as a preview feature, stable in Java 17)
// Records are a special kind of class designed for immutable data
// They automatically generate:
// - Private final fields for each component
// - Public getter methods (without 'get' prefix, e.g., userId() instead of getUserId())
// - equals(), hashCode(), and toString() methods
// - A canonical constructor that takes all components
// - No setter methods (records are immutable)

// This record represents a request to create a new booking
// It's used as the input DTO (Data Transfer Object) for the booking creation API
// DTOs are used to transfer data between different layers of the application
// They help decouple the API contract from the internal domain model
// The validation annotations ensure that invalid data is rejected before processing
public record BookingRequest(

        // The ID of the user making the booking
        // @NotNull ensures this field cannot be null
        // This will be validated by the user-service to ensure the user exists
        @NotNull Long userId,

        // The ID of the hotel where the booking is being made
        // @NotNull ensures this field cannot be null
        // This will be validated by the hotel-service to ensure the hotel exists
        @NotNull Long hotelId,

        // The ID of the specific room being booked
        // @NotNull ensures this field cannot be null
        // This will be validated by the inventory-service to ensure availability
        @NotNull Long roomId,

        // The total amount to be charged for the booking
        // @NotNull ensures it's not null, @DecimalMin("0.0") ensures it's not negative
        // BigDecimal is used for precise money calculations to avoid floating-point errors
        @NotNull @DecimalMin("0.0") BigDecimal amount,

        // The currency code for the amount (e.g., "USD", "INR")
        // @NotBlank ensures it's not null, empty, or just whitespace
        @NotBlank String currency,

        // The payment method chosen by the user (e.g., "CREDIT_CARD", "DEBIT_CARD")
        // @NotBlank ensures it's not null, empty, or just whitespace
        @NotBlank String paymentMethod,

        // The card number for payment processing
        // @NotBlank ensures it's not null, empty, or just whitespace
        // In a real application, this would be tokenized for security, not stored as plain text
        @NotBlank String cardNumber,

        // The check-in date for the booking
        // @NotNull ensures it's not null, @Future ensures it's a future date (not past or present)
        // You can't book a room for today or past dates
        @NotNull @Future LocalDate startDate,

        // The check-out date for the booking
        // @NotNull ensures it's not null, @Future ensures it's a future date
        // The end date must be after the start date (validated in the service layer)
        @NotNull @Future LocalDate endDate

        // The record automatically provides a constructor with all these parameters
        // And getter methods like userId(), hotelId(), etc.
) {
    // Records can have additional methods or constructors, but this one is simple
    // The empty body means we use the default generated constructor and methods
}
