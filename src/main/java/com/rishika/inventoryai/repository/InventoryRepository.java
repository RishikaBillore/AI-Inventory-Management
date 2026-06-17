package com.rishika.inventoryai.repository;
import com.rishika.inventoryai.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
}