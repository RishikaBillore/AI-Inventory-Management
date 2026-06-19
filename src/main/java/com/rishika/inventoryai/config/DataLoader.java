package com.rishika.inventoryai.config;

import com.rishika.inventoryai.model.InventoryItem;
import com.rishika.inventoryai.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.rishika.inventoryai.model.User;
import com.rishika.inventoryai.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository repository;
    private final UserRepository userRepository;

    public DataLoader(
        InventoryRepository repository,
        UserRepository userRepository) {

    this.repository = repository;
    this.userRepository = userRepository;
}

    @Override
    public void run(String... args) throws Exception {

        // Guard: only seed if the database is empty.
        // Without this, every server restart duplicates all rows
        // (H2 is in-memory so it resets anyway, but this is safer
        //  and would be essential if you ever switch to a persistent DB).
        if (repository.count() > 0) {
            System.out.println("Database already has data — skipping seed.");
            return;    
        }
        userRepository.save(
        new User(
                "admin",
                "admin123",
                "ADMIN"
        )
    );

        repository.save(new InventoryItem("Laptop",      "Electronics",   5,   55000, 2));
        repository.save(new InventoryItem("Mouse",       "Electronics",  50,     500, 10));
        repository.save(new InventoryItem("Keyboard",    "Electronics",   0,    1200, 8));
        repository.save(new InventoryItem("Monitor",     "Electronics",   3,   12000, 5));

        repository.save(new InventoryItem("Rice Bag",    "Grocery",     100,      60, 20));
        repository.save(new InventoryItem("Wheat Flour", "Grocery",      15,      40, 25));
        repository.save(new InventoryItem("Sugar",       "Grocery",       0,      45, 15));

        repository.save(new InventoryItem("Shampoo",     "Personal Care", 8,     120, 10));
        repository.save(new InventoryItem("Soap",        "Personal Care", 30,     25, 10));
        repository.save(new InventoryItem("Toothpaste",  "Personal Care", 2,      90, 5));

        System.out.println("Sample inventory data loaded into H2 database!");
    }
}