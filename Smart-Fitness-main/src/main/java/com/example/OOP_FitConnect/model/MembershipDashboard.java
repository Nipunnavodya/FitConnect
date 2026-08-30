package com.example.OOP_FitConnect.model;

public class MembershipDashboard {
    private Long userId;
    private String userName;
    private String membershipType;
    private String expiryDate;
    private int workoutsCompleted;
    private boolean isPaymentDue;

    // Default constructor
    public MembershipDashboard() {
    }

    public MembershipDashboard(Long userId, String userName, String membershipType, String expiryDate, int workoutsCompleted, boolean isPaymentDue) {
        this.userId = userId;
        this.userName = userName;
        this.membershipType = membershipType;
        this.expiryDate = expiryDate;
        this.workoutsCompleted = workoutsCompleted;
        this.isPaymentDue = isPaymentDue;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getWorkoutsCompleted() {
        return workoutsCompleted;
    }

    public void setWorkoutsCompleted(int workoutsCompleted) {
        this.workoutsCompleted = workoutsCompleted;
    }

    public boolean isPaymentDue() {
        return isPaymentDue;
    }

    public void setPaymentDue(boolean paymentDue) {
        isPaymentDue = paymentDue;
    }

    @Override
    public String toString() {
        return "MembershipDashboard{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", membershipType='" + membershipType + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", workoutsCompleted=" + workoutsCompleted +
                ", isPaymentDue=" + isPaymentDue +
                '}';
    }
}
