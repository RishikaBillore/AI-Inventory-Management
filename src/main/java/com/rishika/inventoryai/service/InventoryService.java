package com.rishika.inventoryai.service;

import com.rishika.inventoryai.model.InventoryItem;
import com.rishika.inventoryai.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import com.rishika.inventoryai.dto.InventoryResponseDto;
import com.rishika.inventoryai.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import com.rishika.inventoryai.repository.InventoryTransactionRepository;
import com.rishika.inventoryai.model.InventoryTransaction;
import java.time.LocalDateTime;

@Service
public class InventoryService {

    private final InventoryRepository repository;
    private final InventoryTransactionRepository transactionRepository;

   public InventoryService(
        InventoryRepository repository,
        InventoryTransactionRepository transactionRepository) {

    this.repository = repository;
    this.transactionRepository = transactionRepository;
}

    // ➕ Add item
    public InventoryItem addItem(InventoryItem item) {
        return repository.save(item);
    }

    
    public List<InventoryItem> getAllItems() {
        return repository.findAll();
    }

    public Page<InventoryItem> getAllItems(
        int page,
        int size) {

    return repository.findAll(
            PageRequest.of(page, size));
    }


    public InventoryItem getItemById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
        new ResourceNotFoundException(
                "Item not found with id " + id));
    }

   
    public void deleteItem(Long id) {
        repository.deleteById(id);
    }

    
    public InventoryItem updateQuantity(Long id, int quantity) {

    InventoryItem item = getItemById(id);

    int oldQuantity = item.getQuantity();

    item.setQuantity(quantity);

    InventoryItem updatedItem =
            repository.save(item);

    String type;

    if(quantity > oldQuantity) {
        type = "STOCK_IN";
    } else {
        type = "STOCK_OUT";
    }

    InventoryTransaction transaction =
            new InventoryTransaction(
                    type,
                    Math.abs(quantity - oldQuantity),
                    LocalDateTime.now(),
                    updatedItem
            );

    transactionRepository.save(transaction);

    return updatedItem;
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

private InventoryResponseDto mapToDto(
        InventoryItem item) {

    return new InventoryResponseDto(
            item.getId(),
            item.getName(),
            item.getCategory(),
            item.getQuantity(),
            item.getPrice(),
            item.getReorderLevel()
    );
}
public InventoryResponseDto getItemDto(Long id) {

    InventoryItem item = getItemById(id);

    return mapToDto(item);
}
public List<InventoryResponseDto> getAllItemDtos() {

    return repository.findAll()
            .stream()
            .map(this::mapToDto)
            .toList();
}
}