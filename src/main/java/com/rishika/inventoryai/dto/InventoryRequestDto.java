package com.rishika.inventoryai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class InventoryRequestDto {

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

    public InventoryRequestDto() {}

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
}