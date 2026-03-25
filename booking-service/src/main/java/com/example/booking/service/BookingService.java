package com.example.booking.service;

// Importing Feign client interfaces for calling other microservices
// These are generated proxy classes that make HTTP calls look like regular method calls
// Feign clients handle serialization, deserialization, and error handling automatically
import com.example.booking.client.InventoryServiceClient;
import com.example.booking.client.PaymentServiceClient;
import com.example.booking.client.UserServiceClient;

// Importing domain entities and enums
// These represent the core business objects and their states
import com.example.booking.domain.Booking;
import com.example.booking.domain.BookingStatus;

// Importing DTOs (Data Transfer Objects) for API communication
// DTOs are used to transfer data between layers and services
// They help decouple the internal domain model from external APIs
import com.example.booking.dto.BookingEvent;
import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;
import com.example.booking.dto.InventoryActionResponse;
import com.example.booking.dto.InventoryAdjustmentRequest;
import com.example.booking.dto.PaymentRequest;
import com.example.booking.dto.PaymentResponse;
import com.example.booking.dto.UserResponse;

// Importing the repository for database operations
// The repository pattern abstracts data access logic
import com.example.booking.repository.BookingRepository;

// Importing Resilience4j circuit breaker classes
// Circuit breakers prevent cascading failures in distributed systems
// They automatically stop calling failing services and provide fallback responses
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

// Importing logging framework
// SLF4J is a logging facade that allows different logging implementations
// Logger is used to record application events, errors, and debugging information
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Importing Spring AMQP for RabbitMQ messaging
// RabbitTemplate is used to send messages to message queues
// This enables asynchronous communication between microservices
import org.springframework.amqp.rabbit.core.RabbitTemplate;

// Importing Spring dependency injection utilities
// ObjectProvider helps with circular dependencies and proxying
import org.springframework.beans.factory.ObjectProvider;

// Importing Spring configuration annotations
// @Value injects values from application properties
import org.springframework.beans.factory.annotation.Value;

// Importing Spring Cloud Circuit Breaker
// CircuitBreakerFactory creates circuit breakers for service calls
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

// Importing Spring web utilities
// HttpStatus defines HTTP status codes for responses
import org.springframework.http.HttpStatus;

// Importing Spring service annotation
// @Service marks this as a service component in the business logic layer
import org.springframework.stereotype.Service;

// Importing Spring transaction management
// @Transactional ensures database operations are atomic and consistent
import org.springframework.transaction.annotation.Transactional;

// Importing Spring web exception handling
// ResponseStatusException provides a way to return HTTP errors with custom messages
import org.springframework.web.server.ResponseStatusException;

// Importing Java time utilities
// OffsetDateTime represents timestamps with timezone information
import java.time.OffsetDateTime;

// Importing Java collections
// List is used for collections of objects
import java.util.List;

// Importing Java utilities
// UUID generates unique identifiers for booking references
import java.util.UUID;

// This annotation marks this class as a Spring service component
// Services contain business logic and orchestrate operations across multiple components
// Spring will automatically create an instance of this class and manage its dependencies
@Service

// This annotation enables transactional behavior for all public methods
// Transactions ensure that database operations are atomic - either all succeed or all fail
// This prevents data corruption if something goes wrong during booking creation
@Transactional
public class BookingService {

    // Logger for recording important events and debugging information
    // Loggers help developers understand what the application is doing
    // Different log levels: ERROR, WARN, INFO, DEBUG, TRACE
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    // Constant for the circuit breaker name used with payment service
    // This name must match the configuration in application.yml
    // Circuit breakers have names so they can be configured individually
    private static final String PAYMENT_SERVICE_CIRCUIT_BREAKER = "paymentServiceCircuitBreaker";

    // Constant to identify fallback responses from circuit breaker
    // This helps distinguish between real payment failures and circuit breaker fallbacks
    private static final String PAYMENT_FALLBACK_STATUS = "FALLBACK";

    // Dependency injection fields - Spring will automatically provide these objects
    // These are the collaborators that this service needs to do its work

    // Repository for database operations on bookings
    // Handles saving, finding, and querying booking records
    private final BookingRepository bookingRepository;

    // Feign client for calling the user service
    // Used to validate that users exist before creating bookings
    private final UserServiceClient userServiceClient;

    // Feign client for calling the inventory service
    // Used to reserve and release room inventory
    private final InventoryServiceClient inventoryServiceClient;

    // Feign client for calling the payment service
    // Used to process credit card payments
    private final PaymentServiceClient paymentServiceClient;

    // Provider for getting a proxied version of this service
    // Needed because circuit breakers work through AOP (Aspect-Oriented Programming)
    // Direct method calls bypass AOP, so we need the Spring proxy
    private final ObjectProvider<BookingService> bookingServiceProvider;

    // Template for sending messages to RabbitMQ
    // Used to publish booking events for asynchronous processing
    private final RabbitTemplate rabbitTemplate;

    // Factory for creating circuit breakers
    // Used for user and inventory service calls (payment uses annotation)
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    // Configuration values injected from application properties
    // These control messaging behavior and can be changed without code changes
    private final String exchangeName;
    private final String routingKey;

    // Constructor with dependency injection
    // Spring calls this constructor and provides all the required dependencies
    // This is called "constructor injection" and is the preferred way in Spring
    public BookingService(BookingRepository bookingRepository,
                          UserServiceClient userServiceClient,
                          InventoryServiceClient inventoryServiceClient,
                          PaymentServiceClient paymentServiceClient,
                          ObjectProvider<BookingService> bookingServiceProvider,
                          RabbitTemplate rabbitTemplate,
                          CircuitBreakerFactory<?, ?> circuitBreakerFactory,
                          // @Value injects from properties, with default values if not configured
                          @Value("${app.messaging.booking-exchange:booking.exchange}") String exchangeName,
                          @Value("${app.messaging.booking-created-routing-key:booking.created}") String routingKey) {

        // Store all the injected dependencies as instance variables
        // These will be used throughout the class methods
        this.bookingRepository = bookingRepository;
        this.userServiceClient = userServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.bookingServiceProvider = bookingServiceProvider;
        this.rabbitTemplate = rabbitTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    // This is the main business method that orchestrates the entire booking process
    // It coordinates multiple microservices and handles various failure scenarios
    // The booking process follows this sequence:
    // 1. Validate input dates
    // 2. Generate unique booking reference
    // 3. Validate user exists (calls user-service)
    // 4. Reserve inventory (calls inventory-service)
    // 5. Process payment (calls payment-service with circuit breaker)
    // 6. Save booking to database
    // 7. Publish event to RabbitMQ
    // 8. Return response to client
    // This method demonstrates the Saga pattern - coordinating multiple services with compensation
    public BookingResponse createBooking(BookingRequest request) {
        // First validation: Ensure dates make sense before doing expensive operations
        // This fails fast if the client sent invalid dates
        validateDates(request);

        // Generate a unique booking reference that will be used across all systems
        // This allows tracking the same booking in different services
        // Format: BOOK-XXXXXXXX (8-character uppercase alphanumeric)
        String bookingReference = buildBookingReference();

        // Step 1: User Validation
        // Call user-service to verify the user exists
        // This prevents bookings for non-existent users
        // Uses circuit breaker to handle user-service failures gracefully
        UserResponse user = fetchUser(request.userId());

        // If user validation fails, save the failed booking and return error
        // This creates an audit trail even for failed bookings
        if (user == null) {
            // Create a booking record with failure status for tracking
            Booking booking = saveBooking(request, bookingReference, null, BookingStatus.USER_NOT_FOUND);
            // Publish event so notification service can inform user of failure
            publishBookingEvent(booking, null, "User could not be validated");
            // Return user-friendly error message
            return toResponse(booking, "User could not be validated");
        }

        // Step 2: Inventory Reservation
        // Reserve the room before payment to prevent double-booking
        // This is critical - we don't want to charge a card for an unavailable room
        // Reserve 1 room (quantity = 1) for this booking
        InventoryActionResponse inventoryReservation = reserveInventory(request.roomId(), 1);

        // If inventory reservation fails, save failed booking and return error
        if (inventoryReservation == null || !inventoryReservation.success()) {
            // Save booking with inventory failure status
            Booking booking = saveBooking(request, bookingReference, null, BookingStatus.INVENTORY_UNAVAILABLE);
            // Publish event with specific failure reason
            publishBookingEvent(booking, user, inventoryReservation == null
                    ? "Inventory service unavailable"
                    : inventoryReservation.message());
            // Return appropriate error message to client
            return toResponse(booking, inventoryReservation == null
                    ? "Inventory service unavailable"
                    : inventoryReservation.message());
        }

        // Step 3: Payment Processing
        // This is the most critical step - charging the customer's card
        // Uses circuit breaker to protect against payment service failures
        // Must use the proxied service instance for AOP to work
        PaymentResponse paymentResponse = bookingServiceProvider.getObject()
                .processPaymentWithCircuitBreaker(request, bookingReference);

        // If payment fails, we need to compensate by releasing the reserved inventory
        if (paymentResponse == null || !paymentResponse.success()) {
            // Compensation: Release the inventory since payment failed
            safeReleaseInventory(request.roomId(), 1);

            // Determine the appropriate status based on failure type
            // Circuit breaker fallback is different from actual payment failure
            BookingStatus paymentStatus = isFallbackPaymentResponse(paymentResponse)
                    ? BookingStatus.DEGRADED  // Service unavailable
                    : BookingStatus.PAYMENT_FAILED;  // Payment rejected

            // Save the failed booking with appropriate status
            Booking booking = saveBooking(
                    request,
                    bookingReference,
                    paymentResponse == null ? null : paymentResponse.transactionId(),
                    paymentResponse == null ? BookingStatus.DEGRADED : paymentStatus
            );

            // Build appropriate error message
            String message = paymentResponse == null ? "Payment service unavailable" : paymentResponse.message();

            // Publish failure event for notifications
            publishBookingEvent(booking, user, message);

            // Return failure response to client
            return toResponse(booking, message);
        }

        // SUCCESS PATH: All validations passed, payment succeeded
        // Save the confirmed booking to database
        Booking booking = saveBooking(request, bookingReference, paymentResponse.transactionId(), BookingStatus.CONFIRMED);

        // Publish success event for notifications and analytics
        publishBookingEvent(booking, user, "Booking confirmed");

        // Return success response to client
        return toResponse(booking, "Booking confirmed");
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findAllBookings() {
        // Read every booking and translate entities into API response DTOs.
        return bookingRepository.findAll().stream()
                .map(booking -> toResponse(booking, statusMessage(booking.getStatus())))
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse findBooking(Long bookingId) {
        // Load a single booking or fail with 404 when the id is unknown.
        return bookingRepository.findById(bookingId)
                .map(booking -> toResponse(booking, statusMessage(booking.getStatus())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    private void validateDates(BookingRequest request) {
        // The end date must always be after the start date for a valid hotel booking.
        if (!request.startDate().isBefore(request.endDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate must be before endDate");
        }
    }

    private UserResponse fetchUser(Long userId) {
        // Keep the existing circuit breaker protection for the user microservice.
        return circuitBreakerFactory.create("userService").run(
                () -> userServiceClient.getUser(userId),
                throwable -> null
        );
    }

    private InventoryActionResponse reserveInventory(Long roomId, int quantity) {
        // Keep the existing circuit breaker protection for the inventory microservice.
        return circuitBreakerFactory.create("inventoryService").run(
                () -> inventoryServiceClient.reserveInventory(roomId, new InventoryAdjustmentRequest(quantity)),
                throwable -> null
        );
    }

    // This method must be public so the Spring proxy can wrap it with Resilience4j logic.
    @CircuitBreaker(name = PAYMENT_SERVICE_CIRCUIT_BREAKER, fallbackMethod = "paymentServiceFallback")
    public PaymentResponse processPaymentWithCircuitBreaker(BookingRequest request, String bookingReference) {
        // Build the payment request from the booking payload.
        PaymentRequest paymentRequest = new PaymentRequest(
                bookingReference,
                request.amount(),
                request.currency(),
                request.paymentMethod(),
                request.cardNumber()
        );
        // Execute the Feign call that Resilience4j will monitor for failures.
        return paymentServiceClient.processPayment(paymentRequest);
    }

    // This fallback runs whenever the payment call throws an exception or the circuit is already OPEN.
    public PaymentResponse paymentServiceFallback(BookingRequest request, String bookingReference, Throwable throwable) {
        // Distinguish between an OPEN circuit and a direct downstream failure for clearer logs and messages.
        String fallbackMessage = throwable instanceof CallNotPermittedException
                ? "Payment service circuit breaker is OPEN. Fallback response returned without calling payment-service."
                : "Payment service call failed. Fallback response returned by booking-service.";
        // Log the fallback so operators can see which booking reference was affected.
        log.warn("Payment fallback triggered for booking reference {}", bookingReference, throwable);
        // Return a synthetic payment response that the rest of the booking flow can understand.
        return new PaymentResponse(
                false,
                fallbackMessage,
                null,
                PAYMENT_FALLBACK_STATUS
        );
    }

    private boolean isFallbackPaymentResponse(PaymentResponse paymentResponse) {
        // A fallback response is identified by the special status set in paymentServiceFallback.
        return paymentResponse != null && PAYMENT_FALLBACK_STATUS.equalsIgnoreCase(paymentResponse.status());
    }

    private void safeReleaseInventory(Long roomId, int quantity) {
        // Inventory release is best-effort because the booking has already failed at this point.
        try {
            inventoryServiceClient.releaseInventory(roomId, new InventoryAdjustmentRequest(quantity));
        } catch (Exception ex) {
            log.warn("Failed to release inventory for room {}", roomId, ex);
        }
    }

    private Booking saveBooking(BookingRequest request,
                                String bookingReference,
                                String transactionId,
                                BookingStatus status) {
        // Save a complete booking snapshot so the API and event stream share the same state.
        return bookingRepository.save(new Booking(
                bookingReference,
                request.userId(),
                request.hotelId(),
                request.roomId(),
                request.amount(),
                request.currency(),
                transactionId,
                request.startDate(),
                request.endDate(),
                status,
                OffsetDateTime.now()
        ));
    }

    private void publishBookingEvent(Booking booking, UserResponse user, String message) {
        // Publish the booking event so notification or analytics services can react asynchronously.
        rabbitTemplate.convertAndSend(exchangeName, routingKey, new BookingEvent(
                booking.getBookingReference(),
                booking.getUserId(),
                booking.getRoomId(),
                booking.getStatus().name(),
                user == null ? null : user.email(),
                user == null ? null : user.phone(),
                message,
                booking.getCreatedAt()
        ));
    }

    private BookingResponse toResponse(Booking booking, String message) {
        // Convert the entity into the response format expected by the REST controller.
        return new BookingResponse(
                booking.getId(),
                booking.getBookingReference(),
                booking.getStatus().name(),
                message,
                booking.getTransactionId(),
                booking.getUserId(),
                booking.getRoomId(),
                booking.getCreatedAt()
        );
    }

    private String buildBookingReference() {
        // Generate a short human-readable reference instead of exposing the database id.
        return "BOOK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String statusMessage(BookingStatus status) {
        // Map each internal booking status to a readable API message.
        return switch (status) {
            case CONFIRMED -> "Booking confirmed";
            case PAYMENT_FAILED -> "Payment failed";
            case INVENTORY_UNAVAILABLE -> "Inventory unavailable";
            case USER_NOT_FOUND -> "User could not be validated";
            case DEGRADED -> "Dependent service unavailable";
            case PENDING -> "Booking pending";
        };
    }
}
