package com.example.payment.service;

import com.example.payment.domain.PaymentStatus;
import com.example.payment.domain.PaymentTransaction;
import com.example.payment.dto.PaymentProcessedEvent;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.dto.PaymentTransactionResponse;
import com.example.payment.repository.PaymentTransactionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentTransactionRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;

    public PaymentService(PaymentTransactionRepository repository,
                          RabbitTemplate rabbitTemplate,
                          @Value("${app.messaging.payment-exchange:payment.exchange}") String exchangeName,
                          @Value("${app.messaging.payment-routing-key:payment.processed}") String routingKey) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        boolean successful = request.amount().signum() > 0 && request.cardNumber().trim().length() >= 4;
        PaymentStatus status = successful ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        PaymentTransaction transaction = repository.save(new PaymentTransaction(
                request.bookingReference(),
                request.amount(),
                request.currency(),
                request.paymentMethod(),
                UUID.randomUUID().toString(),
                status,
                OffsetDateTime.now()
        ));

        rabbitTemplate.convertAndSend(exchangeName, routingKey, new PaymentProcessedEvent(
                transaction.getBookingReference(),
                transaction.getTransactionId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getStatus().name(),
                transaction.getProcessedAt()
        ));

        return new PaymentResponse(
                successful,
                successful ? "Payment processed successfully" : "Payment declined",
                transaction.getTransactionId(),
                transaction.getStatus().name()
        );
    }

    @Transactional(readOnly = true)
    public List<PaymentTransactionResponse> findAllPayments() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentTransactionResponse toResponse(PaymentTransaction transaction) {
        return new PaymentTransactionResponse(
                transaction.getId(),
                transaction.getBookingReference(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getPaymentMethod(),
                transaction.getTransactionId(),
                transaction.getStatus().name(),
                transaction.getProcessedAt()
        );
    }
}

