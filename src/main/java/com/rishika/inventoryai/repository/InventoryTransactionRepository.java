package com.rishika.inventoryai.repository;

import com.rishika.inventoryai.model.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, Long> {
}