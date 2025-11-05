package com.paypal.notificationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.paypal.notificationservice.entity.Notification;
//import com.paypal.notificationservice.entity.Transaction;

import com.paypal.transaction_service.entity.Transaction ;



import com.paypal.notificationservice.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;

    public NotificationConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;

        // Setup ObjectMapper with JavaTimeModule to handle LocalDateTime
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "tnx-initiated", groupId = "notification-group")
    public void consumeTransaction(Transaction transaction) {
        System.out.println("📥 Received transaction: " + transaction);

        Notification notification = new Notification();
        notification.setUserId(transaction.getSenderId());
        notification.setMessage("💰 ₹" + transaction.getAmount() + " received from user " + transaction.getSenderId());
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
        System.out.println("✅ Notification saved: " + notification);
    }

}