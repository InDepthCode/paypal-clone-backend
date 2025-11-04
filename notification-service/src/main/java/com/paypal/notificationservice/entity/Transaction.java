package com.paypal.notificationservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Entity
@Data
@Table(name = "transaction")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction {

    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private Long senderId;

    @Setter
    @Column(nullable = false)
    private Long receiverId;


    @Column(nullable = false)
    private Double amount;

    @Setter
    @Column(nullable = false)
    private LocalDate timestamp;

    @Setter
    @Column(nullable = false)
    private String status;

    public Transaction() {}



    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDate.now();
        }
        if (status == null) {
            status = "PENDING";
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                '}';
    }
}