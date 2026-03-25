package com.example.hotel;

// Importing Spring Boot classes
// SpringApplication is used to bootstrap and launch the Spring Boot application
// SpringBootApplication is an annotation that combines multiple configurations
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This annotation marks this class as the main configuration class for the Spring Boot application
// It enables:
// - Auto-configuration: Spring Boot automatically configures beans based on classpath
// - Component scanning: Spring looks for components in this package and subpackages
// - Configuration: Allows defining additional beans and configurations
// In simple terms, this tells Spring Boot to set up the application with sensible defaults
@SpringBootApplication

// This is the main class of the hotel-service microservice
// Every Spring Boot application needs a main class with a main method to start the application
// When you run this class, it starts the entire Spring Boot application
// The hotel-service is responsible for managing hotels and hotel rooms
// It provides APIs for creating hotels, adding rooms, and querying hotel information
public class HotelServiceApplication {

    // The main method is the entry point of any Java application
    // In Spring Boot, this method calls SpringApplication.run() to start the application
    // The args parameter contains command-line arguments (though not used here)
    public static void main(String[] args) {
        // This line starts the Spring Boot application
        // It creates an ApplicationContext, scans for components, configures beans, starts embedded Tomcat
        // HotelServiceApplication.class tells Spring which class contains the main configuration
        // args are passed through to allow command-line configuration if needed
        SpringApplication.run(HotelServiceApplication.class, args);
    }
}
