package com.example.booking.config;

// Importing Jackson ObjectMapper for JSON processing
// ObjectMapper converts Java objects to JSON and vice versa
import com.fasterxml.jackson.databind.ObjectMapper;

// Importing Spring AMQP (Advanced Message Queuing Protocol) classes
// These are RabbitMQ-specific classes for messaging
// TopicExchange - a type of exchange that routes messages based on routing keys with wildcards
// Binding - connects a queue to an exchange with a routing key
// Queue - a buffer that stores messages
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;

// Importing message converter classes
// MessageConverter handles serialization/deserialization of messages
// Jackson2JsonMessageConverter uses Jackson to convert objects to/from JSON
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

// Importing Spring configuration annotations
// @Value injects values from application properties
// @Bean tells Spring that this method returns a bean to be managed by the container
// @Configuration marks this class as containing bean definitions
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This annotation marks this class as a Spring configuration class
// Configuration classes define beans and other Spring configuration
// Spring will process this class and create the defined beans
@Configuration
public class RabbitMessagingConfiguration {

    // This method defines a TopicExchange bean
    // Topic exchanges route messages to queues based on routing key patterns
    // The exchange name comes from configuration properties with a default value
    // Parameters: name, durable (survives broker restart), autoDelete (delete when no bindings)
    @Bean
    TopicExchange bookingExchange(@Value("${app.messaging.booking-exchange:booking.exchange}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    // This method defines a Queue bean
    // Queues store messages until they are consumed by applications
    // QueueBuilder.durable() creates a queue that survives broker restarts
    // The queue name comes from configuration properties
    @Bean
    Queue bookingQueue(@Value("${app.messaging.booking-created-queue:booking.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    // This method defines a Binding bean
    // Bindings connect queues to exchanges with routing keys
    // Messages sent to the exchange with matching routing keys go to the bound queue
    // This binding connects the booking queue to the booking exchange
    @Bean
    Binding bookingBinding(Queue bookingQueue,
                           TopicExchange bookingExchange,
                           @Value("${app.messaging.booking-created-routing-key:booking.created}") String routingKey) {
        // BindingBuilder creates the binding relationship
        // .bind(queue).to(exchange).with(routingKey) sets up the routing
        return BindingBuilder.bind(bookingQueue).to(bookingExchange).with(routingKey);
    }

    // This method defines a MessageConverter bean
    // Message converters handle serialization of message bodies
    // Jackson2JsonMessageConverter converts Java objects to JSON for messaging
    // Uses the provided ObjectMapper for consistent JSON handling
    @Bean
    MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}

// How RabbitMQ messaging works in this system:
// 1. BookingService publishes BookingEvent messages to the TopicExchange
// 2. The exchange routes messages with "booking.created" routing key to the booking queue
// 3. NotificationService consumes messages from the queue asynchronously
// 4. Messages are converted to/from JSON automatically using the MessageConverter

// This configuration ensures the messaging infrastructure is set up when the application starts
// All the exchanges, queues, and bindings are created automatically
