package com.example.booking.web;

// Importing DTOs for API request and response
// BookingRequest contains the data sent by clients to create bookings
// BookingResponse contains the data returned to clients after operations
import com.example.booking.dto.BookingRequest;
import com.example.booking.dto.BookingResponse;

// Importing the business service that contains the booking logic
// Controllers should be thin - they handle HTTP concerns, services handle business logic
import com.example.booking.service.BookingService;

// Importing validation annotation
// @Valid triggers validation of the request body using annotations on BookingRequest
import jakarta.validation.Valid;

// Importing Spring web annotations and utilities
// HttpStatus defines HTTP response codes
import org.springframework.http.HttpStatus;

// Importing Spring MVC annotations for REST endpoints
// @RestController combines @Controller and @ResponseBody - returns JSON automatically
// @RequestMapping defines the base URL path for this controller
// @GetMapping, @PostMapping define HTTP methods and paths
// @RequestBody deserializes JSON request body into Java objects
// @PathVariable extracts values from URL path
// @ResponseStatus sets the HTTP status code for responses
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Importing Java collections
// List is used for returning multiple booking responses
import java.util.List;

// This annotation marks this class as a REST controller
// REST controllers handle HTTP requests and return JSON responses
// Spring automatically converts Java objects to JSON and vice versa
// This controller handles all booking-related API endpoints
@RestController

// This annotation defines the base URL path for all methods in this controller
// All endpoints will be under /api/bookings
// For example: POST /api/bookings, GET /api/bookings, GET /api/bookings/123
@RequestMapping("/api/bookings")
public class BookingController {

    // The business service that contains all booking logic
    // Controllers delegate to services - they don't implement business rules
    // This follows the separation of concerns principle
    private final BookingService bookingService;

    // Constructor injection - Spring provides the BookingService instance
    // This is the preferred way to inject dependencies in Spring
    // No need for @Autowired annotation in modern Spring (constructor injection is automatic)
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // This annotation maps HTTP POST requests to this method
    // POST is used for creating new resources
    // Combined with @RequestMapping("/api/bookings"), this handles POST /api/bookings
    @PostMapping

    // This annotation sets the HTTP response status to 201 CREATED
    // 201 is the standard status for successful resource creation
    @ResponseStatus(HttpStatus.CREATED)

    // Method parameters:
    // @Valid - triggers validation of the BookingRequest using its annotations
    // @RequestBody - tells Spring to deserialize the JSON request body into a BookingRequest object
    // The method returns a BookingResponse which Spring converts to JSON
    public BookingResponse createBooking(@Valid @RequestBody BookingRequest request) {
        // Delegate to the service layer to handle the business logic
        // The controller's job is just to adapt HTTP to method calls
        return bookingService.createBooking(request);
    }

    // This annotation maps HTTP GET requests to this method
    // GET is used for retrieving data
    // Combined with @RequestMapping("/api/bookings"), this handles GET /api/bookings
    // Returns a list of all bookings
    @GetMapping
    public List<BookingResponse> getBookings() {
        // Delegate to service to get all bookings
        // Service returns List<BookingResponse>, which becomes JSON array
        return bookingService.findAllBookings();
    }

    // This annotation maps HTTP GET requests with a path variable
    // {bookingId} is a placeholder that gets replaced with actual values
    // For example: GET /api/bookings/123 calls this method with bookingId = 123
    @GetMapping("/{bookingId}")

    // @PathVariable extracts the value from the URL path
    // The name "bookingId" matches the {bookingId} placeholder
    // Spring converts the string from URL to Long automatically
    public BookingResponse getBooking(@PathVariable Long bookingId) {
        // Delegate to service to find a specific booking
        // If not found, service throws exception which becomes HTTP 404
        return bookingService.findBooking(bookingId);
    }
}
