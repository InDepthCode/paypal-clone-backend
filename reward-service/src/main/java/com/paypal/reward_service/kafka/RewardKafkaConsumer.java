package com.paypal.reward_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.reward_service.entity.Reward;
import com.paypal.reward_service.entity.Transaction;
import com.paypal.reward_service.repository.RewardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class RewardKafkaConsumer {

    private final RewardRepository rewardRepository;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public RewardKafkaConsumer(RewardRepository rewardRepository, KafkaTemplate<String, Transaction> kafkaTemplate, ObjectMapper objectMapper) {
        this.rewardRepository = rewardRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "tnx-initiated", groupId = "reward-group")
    public void consumeTransaction(Transaction transaction) {

        try{

            if(rewardRepository.existsByTransactionId(transaction.getId())){
                System.out.println("⚠️ Reward already exists for transaction: " + transaction.getId());
                return;
            }

            Reward reward = new Reward();
            reward.setUserId(transaction.getSenderId());
            reward.setTransactionId(transaction.getId());
            reward.setPoints(transaction.getAmount() * 100);
            reward.setSentAt(LocalDateTime.now());


            rewardRepository.save(reward);
            log.info("reward saved: " + reward);
                    }
        catch (Exception e){
            System.err.println("❌ Failed to process transaction " + transaction.getId() + ": " + e.getMessage());
            throw e; // Let Spring Kafka handle the retry
        }

    }
}
