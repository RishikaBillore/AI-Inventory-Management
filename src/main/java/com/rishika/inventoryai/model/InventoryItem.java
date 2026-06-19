package com.rishika.inventoryai.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "item")
    private List<InventoryTransaction> transactions;

    @NotBlank(message = "Name is required")
private String name;

@NotBlank(message = "Category is required")
private String category;

@Min(value = 0, message = "Quantity cannot be negative")
private int quantity;

@Min(value = 1, message = "Price must be greater than 0")
private double price;

@Min(value = 0, message = "Reorder level cannot be negative")
private int reorderLevel;

    // Constructors
    public InventoryItem() {}

    public InventoryItem(String name, String category, int quantity, double price, int reorderLevel) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.reorderLevel = reorderLevel;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public List<InventoryTransaction> getTransactions() {
    return transactions;
}

    public void setTransactions(
            List<InventoryTransaction> transactions) {
        this.transactions = transactions;
    }
}