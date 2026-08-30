package com.example.OOP_FitConnect.controller;

import com.example.OOP_FitConnect.model.MembershipDashboard;
import com.example.OOP_FitConnect.service.MembershipDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class MembershipDashboardController {

    private final MembershipDashboardService dashboardService;

    @Autowired
    public MembershipDashboardController(MembershipDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{userId}")
    public String viewDashboard(@PathVariable Long userId, Model model) {
        Optional<MembershipDashboard> dashboard = dashboardService.getDashboardByUserId(userId);
        if (dashboard.isPresent()) {
            model.addAttribute("dashboard", dashboard.get());
            return "member_dashboard"; // Assuming your HTML file is named member_dashboard.html
        } else {
            // Handle case where dashboard is not found (e.g., show an error page)
            return "error/dashboard-not-found";
        }
    }

    @GetMapping("/all")
    public String viewAllDashboards(Model model) {
        List<MembershipDashboard> allDashboards = dashboardService.getAllDashboards();
        model.addAttribute("dashboards", allDashboards);
        return "all_dashboards"; // You would need to create an all_dashboards.html template
    }

    // You can add more controller methods as needed
}
