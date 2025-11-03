package com.paypal.transaction_service.dto;

import lombok.Data;

@Data
public class TransferRequest {

    private String senderName;
    private String receiverName;
    private Double amount;
}
