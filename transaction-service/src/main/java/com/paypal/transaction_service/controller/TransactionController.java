package com.paypal.transaction_service.controller;

import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTransaction (@RequestBody Transaction transaction) {
       Transaction created = transactionService.createTransaction(transaction);
       return ResponseEntity.ok(created);
    }


    @GetMapping("/all")
    public List<?> getAll(){
        return transactionService.getAllTransactions();
    }

}
