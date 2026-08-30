package model;

import java.util.UUID;

public class Equipment {
    private String id;
    private String name;
    private String description;
    private int quantity;
    private String status; // "Available" or "In Use"
    private String lastServiced;

    public Equipment() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters and setters...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLastServiced() { return lastServiced; }
    public void setLastServiced(String lastServiced) { this.lastServiced = lastServiced; }
}
