package com.rishika.inventoryai.controller;

import com.rishika.inventoryai.model.InventoryTransaction;
import com.rishika.inventoryai.repository.InventoryTransactionRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final InventoryTransactionRepository repository;

    public TransactionController(
            InventoryTransactionRepository repository) {

        this.repository = repository;
    }

    @GetMapping
    public List<InventoryTransaction> getAllTransactions() {
        return repository.findAll();
    }
}