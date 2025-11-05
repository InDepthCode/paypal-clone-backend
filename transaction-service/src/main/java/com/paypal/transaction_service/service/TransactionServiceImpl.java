package com.paypal.transaction_service.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.transaction_service.dto.TransferRequest;
import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.kafka.KafkaEventProducer;
import com.paypal.transaction_service.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ObjectMapper objectMapper;
    private final KafkaEventProducer kafkaEventProducer;

    public TransactionServiceImpl(TransactionRepository transactionRepository, ObjectMapper objectMapper, KafkaEventProducer kafkaEventProducer) {
        this.transactionRepository = transactionRepository;
        this.objectMapper = objectMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public Transaction createTransaction(TransferRequest request) {

           /*
            Controller passes a DTO (TransferRequest) to the service.
            The service creates a new Transaction entity from that DTO.
            Then the same entity (Transaction) is saved and sent to Kafka.

            */
        Long senderId = request.getSenderId();
        Long receiverId = request.getReceiverId();
        Double amount = request.getAmount();

        Transaction transaction = new Transaction();
        transaction.setSenderId(senderId);
        transaction.setReceiverId(receiverId);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDate.now());
        transaction.setStatus("SUCCESS");

         Transaction saved =  transactionRepository.save(transaction);

         try{
             String eventPayload = objectMapper.writeValueAsString(saved);
             String key = String.valueOf(saved.getId());
            kafkaEventProducer.sendTransactionEvent(key, saved);
            System.out.println("kafka message event");
         }
         catch(Exception e){
            System.err.println("Failed to send transaction event" + e.getMessage());
            e.printStackTrace();
         }

         return saved;

    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
