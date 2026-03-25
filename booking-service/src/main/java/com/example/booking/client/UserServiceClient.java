package com.example.booking.client;

// Importing the response DTO that will be returned by the user service
// This DTO contains user information needed for booking validation
import com.example.booking.dto.UserResponse;

// Importing Feign client annotation
// Feign is a declarative web service client that makes HTTP calls easy
import org.springframework.cloud.openfeign.FeignClient;

// Importing Spring web annotations
// These are the same annotations used in REST controllers
// Feign uses them to know how to make HTTP calls
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// This annotation declares this interface as a Feign client
// Feign will create a proxy implementation at runtime that makes actual HTTP calls
// The "name" attribute specifies the service name registered in Eureka
// Spring Cloud Load Balancer will resolve "user-service" to actual service instances
@FeignClient(name = "user-service")
public interface UserServiceClient {

    // This annotation maps to HTTP GET requests
    // The URL path includes a placeholder {userId} that gets replaced with the actual user ID
    // Combined with the Feign client base URL, this calls: GET http://user-service/api/users/{userId}
    @GetMapping("/api/users/{userId}")

    // @PathVariable tells Feign to substitute the method parameter into the URL path
    // The parameter name "userId" matches the {userId} placeholder
    // Feign handles converting the Long to string for the URL
    // The return type UserResponse tells Feign how to deserialize the JSON response
    UserResponse getUser(@PathVariable Long userId);
}

// How Feign works:
// 1. At runtime, Spring creates a proxy that implements this interface
// 2. When getUser(userId) is called, the proxy makes an HTTP GET request
// 3. The request goes to the user-service (discovered via Eureka)
// 4. The JSON response is automatically converted to a UserResponse object
// 5. If the service is down, an exception is thrown (handled by circuit breakers)

// This approach makes inter-service communication as simple as calling a local method
// No need to manually construct HTTP requests or handle JSON serialization
