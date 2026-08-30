package com.example.OOP_FitConnect.model;

public class ClassSession {
    private String id;
    private String name;
    private String instructor;
    private String schedule; // e.g., "Monday 10:00-11:00"
    // Add more fields as needed

    // Constructors, getters, setters

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

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
