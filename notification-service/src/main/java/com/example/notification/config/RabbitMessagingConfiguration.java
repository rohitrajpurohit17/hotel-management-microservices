package com.example.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMessagingConfiguration {

    @Bean
    TopicExchange bookingExchange(@Value("${app.messaging.booking-exchange:booking.exchange}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    Queue bookingQueue(@Value("${app.messaging.booking-created-queue:booking.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    Binding bookingBinding(Queue bookingQueue,
                           TopicExchange bookingExchange,
                           @Value("${app.messaging.booking-created-routing-key:booking.created}") String routingKey) {
        return BindingBuilder.bind(bookingQueue).to(bookingExchange).with(routingKey);
    }

    @Bean
    TopicExchange paymentExchange(@Value("${app.messaging.payment-exchange:payment.exchange}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    Queue paymentQueue(@Value("${app.messaging.payment-queue:payment.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    Binding paymentBinding(Queue paymentQueue,
                           TopicExchange paymentExchange,
                           @Value("${app.messaging.payment-routing-key:payment.processed}") String routingKey) {
        return BindingBuilder.bind(paymentQueue).to(paymentExchange).with(routingKey);
    }

    @Bean
    MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}

