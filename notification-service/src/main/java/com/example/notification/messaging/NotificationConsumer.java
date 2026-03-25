package com.example.notification.messaging;

import com.example.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final NotificationService notificationService;

    public NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${app.messaging.booking-created-queue:booking.queue}")
    public void handleBookingEvent(BookingEvent event) {
        notificationService.recordBookingNotification(event);
    }

    @RabbitListener(queues = "${app.messaging.payment-queue:payment.queue}")
    public void handlePaymentEvent(PaymentProcessedEvent event) {
        notificationService.recordPaymentNotification(event);
    }
}

