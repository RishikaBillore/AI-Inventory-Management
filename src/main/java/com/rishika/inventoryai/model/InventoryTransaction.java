package com.rishika.inventoryai.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private int quantity;

    private LocalDateTime timestamp;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "item_id")
    private InventoryItem item;

    public InventoryTransaction() {}

    public InventoryTransaction(
            String type,
            int quantity,
            LocalDateTime timestamp,
            InventoryItem item) {

        this.type = type;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }
}