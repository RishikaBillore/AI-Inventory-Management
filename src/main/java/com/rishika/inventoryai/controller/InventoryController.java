package com.rishika.inventoryai.controller;

import com.rishika.inventoryai.dto.InventoryRequestDto;
import com.rishika.inventoryai.dto.InventoryResponseDto;
import com.rishika.inventoryai.model.InventoryItem;
import com.rishika.inventoryai.service.InventoryService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    // CREATE ITEM USING DTO + VALIDATION

    @PostMapping
    public InventoryItem addItem(
            @Valid
            @RequestBody InventoryRequestDto dto) {

        InventoryItem item = new InventoryItem();

        item.setName(dto.getName());
        item.setCategory(dto.getCategory());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        item.setReorderLevel(dto.getReorderLevel());

        return service.addItem(item);
    }

    // EXISTING ENDPOINT (KEEP FOR FRONTEND)

    @GetMapping
public List<InventoryResponseDto> getAllItems() {
    return service.getAllItemDtos();
}

    // NEW PAGINATION ENDPOINT

    @GetMapping("/paged")
    public Page<InventoryItem> getPagedItems(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getAllItems(page, size);
    }

    @GetMapping("/{id}")
public InventoryResponseDto getItem(
        @PathVariable Long id) {

    return service.getItemDto(id);
}

    @DeleteMapping("/{id}")
    public String deleteItem(
            @PathVariable Long id) {

        service.deleteItem(id);
        return "Item deleted successfully";
    }

    @PutMapping("/{id}/quantity")
    public InventoryItem updateQuantity(

            @PathVariable Long id,

            @RequestParam int quantity) {

        return service.updateQuantity(id, quantity);
    }

    @GetMapping("/{id}/suggestion")
    public String getSuggestion(
            @PathVariable Long id) {

        InventoryItem item =
                service.getItemById(id);

        return service.getSmartSuggestion(item);
    }

    @GetMapping("/summary")
    public String getSummary() {
        return service.getInventorySummary();
    }

    @GetMapping("/low-stock")
    public List<InventoryItem> getLowStock() {
        return service.getLowStockItems();
    }

    @GetMapping("/critical")
    public List<InventoryItem> getCritical() {
        return service.getCriticalItems();
    }

    @GetMapping("/health")
    public String getHealth() {
        return service.getInventoryHealthScore();
    }
}