package com.example.OOP_FitConnect.model;

import java.util.UUID;

public class WorkoutPlan {
    private String id;
    private String name;
    private String description;
    private String schedule;
    private boolean completed;

    public WorkoutPlan() {
        this.id = UUID.randomUUID().toString();
        this.completed = false;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
