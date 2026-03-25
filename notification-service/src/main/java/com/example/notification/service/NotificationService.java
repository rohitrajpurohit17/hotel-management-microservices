package com.example.notification.service;

import com.example.notification.domain.NotificationLog;
import com.example.notification.dto.NotificationResponse;
import com.example.notification.messaging.BookingEvent;
import com.example.notification.messaging.PaymentProcessedEvent;
import com.example.notification.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationLogRepository repository;

    public NotificationService(NotificationLogRepository repository) {
        this.repository = repository;
    }

    public void recordBookingNotification(BookingEvent event) {
        if (event.userEmail() != null && !event.userEmail().isBlank()) {
            repository.save(new NotificationLog(
                    "BOOKING_" + event.status(),
                    "EMAIL",
                    event.userEmail(),
                    event.message(),
                    OffsetDateTime.now()
            ));
        }

        if (event.userPhone() != null && !event.userPhone().isBlank()) {
            repository.save(new NotificationLog(
                    "BOOKING_" + event.status(),
                    "SMS",
                    event.userPhone(),
                    event.message(),
                    OffsetDateTime.now()
            ));
        }

        if ((event.userEmail() == null || event.userEmail().isBlank())
                && (event.userPhone() == null || event.userPhone().isBlank())) {
            repository.save(new NotificationLog(
                    "BOOKING_" + event.status(),
                    "INTERNAL",
                    "n/a",
                    event.message(),
                    OffsetDateTime.now()
            ));
        }
    }

    public void recordPaymentNotification(PaymentProcessedEvent event) {
        repository.save(new NotificationLog(
                "PAYMENT_" + event.status(),
                "INTERNAL",
                event.bookingReference(),
                "Payment " + event.status() + " for transaction " + event.transactionId(),
                OffsetDateTime.now()
        ));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findAllNotifications() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(NotificationLog log) {
        return new NotificationResponse(
                log.getId(),
                log.getEventType(),
                log.getChannel(),
                log.getRecipient(),
                log.getPayload(),
                log.getCreatedAt()
        );
    }
}

