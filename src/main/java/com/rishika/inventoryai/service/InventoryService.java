package com.rishika.inventoryai.service;

import com.rishika.inventoryai.model.InventoryItem;
import com.rishika.inventoryai.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    // ➕ Add item
    public InventoryItem addItem(InventoryItem item) {
        return repository.save(item);
    }

    
    public List<InventoryItem> getAllItems() {
        return repository.findAll();
    }


    public InventoryItem getItemById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

   
    public void deleteItem(Long id) {
        repository.deleteById(id);
    }

    
    public InventoryItem updateQuantity(Long id, int quantity) {
        InventoryItem item = getItemById(id);
        item.setQuantity(quantity);
        return repository.save(item);
    }

    

    public boolean isLowStock(InventoryItem item) {
        return item.getQuantity() <= item.getReorderLevel();
    }

    public String getSmartSuggestion(InventoryItem item) {
        if (item.getQuantity() == 0) {
            return "OUT OF STOCK - RESTOCK IMMEDIATELY";
        } else if (item.getQuantity() <= item.getReorderLevel()) {
            return "Low stock - Consider reordering soon";
        } else if (item.getQuantity() <= item.getReorderLevel() * 2) {
            return "Stock decreasing - monitor closely";
        } else {
            return "Stock level healthy";
        }
    }
    public String getInventorySummary() {
    List<InventoryItem> items = repository.findAll();

    int totalItems = items.size();
    int totalQuantity = items.stream()
            .mapToInt(InventoryItem::getQuantity)
            .sum();

    return "Total Products: " + totalItems +
            ", Total Stock Units: " + totalQuantity;
}
public List<InventoryItem> getLowStockItems() {
    return repository.findAll()
            .stream()
            .filter(item -> item.getQuantity() <= item.getReorderLevel())
            .toList();
}
public List<InventoryItem> getCriticalItems() {
    return repository.findAll()
            .stream()
            .filter(item -> item.getQuantity() == 0)
            .toList();
}

public String getInventoryHealthScore() {
    List<InventoryItem> items = repository.findAll();

    if (items.isEmpty()) {
        return "No inventory data available";
    }

    long healthy = items.stream()
            .filter(item -> item.getQuantity() > item.getReorderLevel() * 2)
            .count();

    long low = items.stream()
            .filter(item -> item.getQuantity() <= item.getReorderLevel()
                    && item.getQuantity() > 0)
            .count();

    long critical = items.stream()
            .filter(item -> item.getQuantity() == 0)
            .count();

    double score = (healthy * 1.0 + low * 0.5) / items.size() * 100;

    return "Health Score: " + String.format("%.2f", score) +
            "% | Healthy: " + healthy +
            ", Low: " + low +
            ", Critical: " + critical;
}
}