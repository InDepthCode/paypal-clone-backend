package com.paypal.transaction_service.kafka;

import com.paypal.transaction_service.entity.Transaction;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaEventProducer {

    private static final String TOPIC = "tnx-initiated";

    private final KafkaTemplate<String, Transaction> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Publishing our transaction event to Kafka
    public void sendTransactionEvent(String key, Transaction transaction) {
        System.out.println("📤 Sending to Kafka → Topic: " + TOPIC + ", Key: " + key + ", Message: " + transaction);

        CompletableFuture<SendResult<String, Transaction>> future = kafkaTemplate.send(TOPIC, key, transaction);

        future.whenComplete((result, ex) -> {
            if (ex == null && result != null) {
                RecordMetadata metadata = result.getRecordMetadata();
                System.out.println("✅ Kafka message sent successfully! Topic: " + metadata.topic()
                        + ", Partition: " + metadata.partition()
                        + ", Offset: " + metadata.offset());
            } else {
                System.err.println("❌ Failed to send Kafka message: " + (ex != null ? ex.getMessage() : "unknown error"));
                if (ex != null) ex.printStackTrace();
            }
        });
    }
}
