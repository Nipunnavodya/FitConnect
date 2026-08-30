package com.example.OOP_FitConnect.model;

import java.util.List;
import java.util.UUID;

public class Plan {
    private String id;
    private String name;
    private double price;
    private String description;
    private List<String> features;

    public Plan() {
        this.id = UUID.randomUUID().toString();
    }

    public Plan(String name, double price, String description, List<String> features) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.description = description;
        this.features = features;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }
}
