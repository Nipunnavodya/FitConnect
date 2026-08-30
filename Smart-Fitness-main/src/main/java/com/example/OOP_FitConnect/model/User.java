package com.example.OOP_FitConnect.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private int verificationCode;
    private String branch;
    private Integer currentPlanId;

    // Stored in DB: USER, INSTRUCTOR, ADMIN
    private String role;
    private List<WorkoutPlan> workoutPlans;
    private Double bmi;
    private String profileImage;
    private String currentPlanName;

    private static final String ADMIN_EMAIL = "admin@user.com";

    public User() {
        this.workoutPlans = new ArrayList<>();
        this.role = "USER";
    }

    // Role helpers
    public String getRole() {
        if (ADMIN_EMAIL.equalsIgnoreCase(this.email)) {
            return "ADMIN";
        }
        return role != null ? role : "USER";
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(getRole());
    }

    public boolean isInstructor() {
        return "INSTRUCTOR".equals(getRole());
    }

    public boolean isGuest() {
        return "GUEST".equals(role);
    }

    public boolean isVerified() {
        return verificationCode == 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getVerificationCode() { return verificationCode; }
    public void setVerificationCode(int verificationCode) { this.verificationCode = verificationCode; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public Double getBmi() { return bmi; }
    public void setBmi(Double bmi) { this.bmi = bmi; }

    public List<WorkoutPlan> getWorkoutPlans() { return workoutPlans; }
    public void setWorkoutPlans(List<WorkoutPlan> workoutPlans) { this.workoutPlans = workoutPlans; }
    public void addWorkoutPlan(WorkoutPlan workoutPlan) { this.workoutPlans.add(workoutPlan); }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Integer getCurrentPlanId() { return currentPlanId; }
    public void setCurrentPlanId(Integer currentPlanId) { this.currentPlanId = currentPlanId; }

    public String getCurrentPlanName() { return currentPlanName; }
    public void setCurrentPlanName(String currentPlanName) { this.currentPlanName = currentPlanName; }
}
