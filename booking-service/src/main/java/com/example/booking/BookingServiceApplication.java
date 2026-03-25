package com.example.booking;

// Importing necessary classes from Spring Boot and Spring Cloud
// SpringApplication is used to bootstrap and launch the Spring Boot application
// SpringBootApplication is an annotation that combines several other annotations to enable auto-configuration, component scanning, and more
// EnableFeignClients is an annotation from Spring Cloud OpenFeign that enables the use of Feign clients for declarative REST client calls to other microservices
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// This annotation marks this class as the main configuration class for the Spring Boot application
// It enables:
// - Auto-configuration: Spring Boot automatically configures beans based on the classpath and properties
// - Component scanning: Spring looks for components (like @Controller, @Service, etc.) in the package and subpackages
// - Configuration: Allows defining additional beans and configurations
// In simple terms, this tells Spring Boot to set up the application with sensible defaults
@SpringBootApplication

// This annotation enables Feign clients in this application
// Feign is a declarative web service client that makes it easy to call REST APIs from other microservices
// With this annotation, Spring will scan for interfaces annotated with @FeignClient and create proxy implementations
// This allows the booking service to easily call other services like user-service, payment-service, etc.
@EnableFeignClients

// This is the main class of the booking-service microservice
// Every Spring Boot application needs a main class with a main method to start the application
// When you run this class, it starts the entire Spring Boot application, including all configured components, controllers, services, etc.
// The booking-service is responsible for handling hotel bookings, coordinating with other services for user validation, payment processing, inventory management, and sending notifications
public class BookingServiceApplication {

    // The main method is the entry point of any Java application
    // In Spring Boot, this method calls SpringApplication.run() to start the application
    // The args parameter contains command-line arguments passed to the application (though not used here)
    public static void main(String[] args) {
        // This line starts the Spring Boot application
        // It creates an ApplicationContext, scans for components, configures beans, starts embedded servers (like Tomcat), and makes the application ready to handle requests
        // BookingServiceApplication.class tells Spring which class contains the main configuration
        // args are passed through to allow command-line configuration if needed
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
