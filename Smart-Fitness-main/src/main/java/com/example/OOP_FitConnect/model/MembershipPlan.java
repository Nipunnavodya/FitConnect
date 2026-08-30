package com.example.OOP_FitConnect.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MembershipPlan {
    private int id;
    private String name;
    private String description;
    private double price;
    private int durationMonths;
    private String features; // pipe-separated: "Feature1|Feature2|Feature3"
    private boolean popular;

    public MembershipPlan() {
    }

    public MembershipPlan(int id, String name, String description, double price, int durationMonths, String features, boolean popular) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMonths = durationMonths;
        this.features = features;
        this.popular = popular;
    }

    // Helper to get features as a list
    public List<String> getFeatureList() {
        if (features == null || features.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(features.split("\\|"));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }

    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }

    public boolean isPopular() { return popular; }
    public void setPopular(boolean popular) { this.popular = popular; }
}


