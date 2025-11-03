package com.paypal.transaction_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="sender_name" , nullable=false)
    private Long senderId;

    @Column(name = "receiver_name", nullable=false)
    private Long receiverId;

    @Column(nullable=false)
    private Double amount;

    @Column(nullable=false)
    private LocalDate timestamp;

    @Column(nullable=false)
    private String status;

    @PrePersist
    public void prePersist() {
        if(timestamp == null) {
            timestamp = LocalDate.now();
        }
        if(status == null) {
            status = "PENDING";
        }
    }

    @Override
    public String toString(){
        return "Transaction [id=" + id + ", senderName=" + senderId + ", receiverName=" + receiverId + ", amount=" + amount + ", timestamp=" + timestamp + ", status=" + status + "]";
    }
}
