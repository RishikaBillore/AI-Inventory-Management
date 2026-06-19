package com.rishika.inventoryai.dto;

public class InventoryResponseDto {

    private Long id;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private int reorderLevel;

    public InventoryResponseDto(
            Long id,
            String name,
            String category,
            int quantity,
            double price,
            int reorderLevel) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.reorderLevel = reorderLevel;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }
}