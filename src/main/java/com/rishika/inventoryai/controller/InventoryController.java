package com.rishika.inventoryai.controller;

import com.rishika.inventoryai.model.InventoryItem;
import com.rishika.inventoryai.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    
    @PostMapping
    public InventoryItem addItem(@RequestBody InventoryItem item) {
        return service.addItem(item);
    }

    
    @GetMapping
    public List<InventoryItem> getAllItems() {
        return service.getAllItems();
    }

    
    @GetMapping("/{id}")
    public InventoryItem getItem(@PathVariable Long id) {
        return service.getItemById(id);
    }

    
    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Long id) {
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
    public String getSuggestion(@PathVariable Long id) {
        InventoryItem item = service.getItemById(id);
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