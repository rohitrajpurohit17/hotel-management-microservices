package com.example.booking.domain;

// Importing JPA (Java Persistence API) annotations for database mapping
// Entity marks this class as a JPA entity, meaning it represents a table in the database
// EnumType and Enumerated are for mapping enum fields to database columns
// GeneratedValue and GenerationType specify how primary keys are generated
// Id marks the primary key field
// Table specifies the table name in the database

// Importing Java classes for data types
// BigDecimal is used for precise decimal numbers like monetary amounts
// LocalDate is for date-only values (without time)
// OffsetDateTime is for date and time with timezone offset
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

// This annotation tells JPA that this class represents a database table
// The @Table annotation specifies the exact table name as "bookings"
// Without @Table, JPA would use the class name "Booking" as the table name
// This entity class represents a booking record in the hotel management system
// A booking is when a customer reserves a room in a hotel for specific dates
@Entity
@Table(name = "bookings")
public class Booking {

    // This annotation marks the 'id' field as the primary key of the table
    // Primary key uniquely identifies each record in the database
    // @GeneratedValue with IDENTITY strategy means the database will automatically generate unique IDs
    // IDENTITY uses auto-increment in databases like MySQL or PostgreSQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // These are the data fields that store information about the booking
    // bookingReference: A unique string identifier for the booking (like a confirmation number)
    // userId: The ID of the user who made the booking (references the user in user-service)
    // hotelId: The ID of the hotel where the booking is made (references hotel in hotel-service)
    // roomId: The specific room being booked (references room in hotel-service)
    // amount: The total cost of the booking (using BigDecimal for precise money calculations)
    // currency: The currency code (like "USD", "INR") for the amount
    // transactionId: The payment transaction ID from the payment service
    // startDate and endDate: The check-in and check-out dates for the booking
    // status: The current status of the booking (using an enum for predefined values)
    // createdAt: Timestamp when the booking was created, including timezone information
    private String bookingReference;
    private Long userId;
    private Long hotelId;
    private Long roomId;
    private BigDecimal amount;
    private String currency;
    private String transactionId;
    private LocalDate startDate;
    private LocalDate endDate;

    // @Enumerated(EnumType.STRING) tells JPA to store the enum value as a string in the database
    // This makes it readable (e.g., "CONFIRMED" instead of a number)
    // BookingStatus is an enum that defines possible states like PENDING, CONFIRMED, CANCELLED, etc.
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private OffsetDateTime createdAt;

    // This is a protected no-argument constructor
    // JPA requires a no-argument constructor to create instances when loading from database
    // It's protected to prevent accidental instantiation from outside the package
    // In JPA, entities should not be instantiated directly; let the framework handle it
    protected Booking() {
    }

    // This is a public constructor that takes all the necessary parameters to create a new Booking
    // It's used when creating new booking instances in the application code
    // All parameters are required to ensure a complete booking record
    // This follows the pattern of immutable objects - once created, the booking data doesn't change
    public Booking(String bookingReference, Long userId, Long hotelId, Long roomId, BigDecimal amount,
                   String currency, String transactionId, LocalDate startDate, LocalDate endDate,
                   BookingStatus status, OffsetDateTime createdAt) {
        // Assigning the parameter values to the instance fields
        // 'this.' refers to the instance variable, distinguishing from the parameter
        this.bookingReference = bookingReference;
        this.userId = userId;
        this.hotelId = hotelId;
        this.roomId = roomId;
        this.amount = amount;
        this.currency = currency;
        this.transactionId = transactionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    // These are getter methods that allow other classes to read the booking data
    // In JPA entities, we typically use getters and setters for encapsulation
    // Getters return the value of private fields
    // No setters are provided here, making this an immutable entity (data can't be changed after creation)
    // This is a good practice for domain objects to prevent accidental data modification

    // Returns the unique database ID of this booking
    public Long getId() {
        return id;
    }

    // Returns the booking reference number (like a confirmation code)
    public String getBookingReference() {
        return bookingReference;
    }

    // Returns the ID of the user who made this booking
    public Long getUserId() {
        return userId;
    }

    // Returns the ID of the hotel for this booking
    public Long getHotelId() {
        return hotelId;
    }

    // Returns the ID of the specific room being booked
    public Long getRoomId() {
        return roomId;
    }

    // Returns the total amount charged for this booking
    public BigDecimal getAmount() {
        return amount;
    }

    // Returns the currency code for the amount (e.g., "USD", "EUR")
    public String getCurrency() {
        return currency;
    }

    // Returns the payment transaction ID from the payment service
    public String getTransactionId() {
        return transactionId;
    }

    // Returns the check-in date for the booking
    public LocalDate getStartDate() {
        return startDate;
    }

    // Returns the check-out date for the booking
    public LocalDate getEndDate() {
        return endDate;
    }

    // Returns the current status of the booking (PENDING, CONFIRMED, etc.)
    public BookingStatus getStatus() {
        return status;
    }

    // Returns the timestamp when this booking was created
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}

