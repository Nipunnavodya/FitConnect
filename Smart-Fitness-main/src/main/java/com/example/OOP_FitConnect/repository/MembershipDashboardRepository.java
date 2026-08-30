package com.example.OOP_FitConnect.repository;

import com.example.OOP_FitConnect.model.MembershipDashboard;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MembershipDashboardRepository {

    private final List<MembershipDashboard> dashboards = new ArrayList<>();

    public MembershipDashboardRepository() {
        // Initialize with some sample data
        dashboards.add(new MembershipDashboard(1L, "John Doe", "Premium", "2025-06-30", 25, false));
        dashboards.add(new MembershipDashboard(2L, "Jane Smith", "Basic", "2025-05-20", 10, true));
        // Add more sample data as needed
    }

    public Optional<MembershipDashboard> findByUserId(Long userId) {
        return dashboards.stream()
                .filter(dashboard -> dashboard.getUserId().equals(userId))
                .findFirst();
    }

    public List<MembershipDashboard> findAll() {
        return new ArrayList<>(dashboards);
    }

    // You might add methods for updating dashboard information later
}
