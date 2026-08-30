package com.example.OOP_FitConnect.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.OOP_FitConnect.model.MembershipPlan;
import com.example.OOP_FitConnect.model.Payment;
import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.repository.DBController;
import com.example.OOP_FitConnect.service.GuestService;
import com.example.OOP_FitConnect.service.PaymentHistoryService;
import com.example.OOP_FitConnect.service.PlanService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MembershipController {

    @Autowired
    private GuestService guestService;

    @Autowired
    private PlanService planService;

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @Autowired
    private DBController dbController;

    // Get all plans (JSON API for frontend)
    @GetMapping("/plans")
    @ResponseBody
    public ResponseEntity<List<MembershipPlan>> getAllMembershipPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    // Subscribe to a plan (user action)
    @PostMapping("/api/subscribe")
    @ResponseBody
    public ResponseEntity<?> subscribeToPlan(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not logged in"));
        }

        int userId = (Integer) session.getAttribute("userId");
        int planId = ((Number) body.get("planId")).intValue();
        String paymentMethod = (String) body.getOrDefault("paymentMethod", "Credit Card");

        MembershipPlan plan = planService.getPlanById(planId);
        if (plan == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Plan not found"));
        }

        // Create payment record
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setPlanId(planId);
        payment.setAmount(plan.getPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("completed");
        payment.setPaymentDate(LocalDateTime.now());
        paymentHistoryService.addPayment(payment);

        // Update user's current plan
        dbController.updateUserPlan(userId, planId);

        return ResponseEntity.ok(Map.of("message", "Subscription successful", "planName", plan.getName()));
    }

    // Get current user's plan info
    @GetMapping("/api/my-plan")
    @ResponseBody
    public ResponseEntity<?> getMyPlan(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not logged in"));
        }

        int userId = (Integer) session.getAttribute("userId");
        User user = guestService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        if (user.getCurrentPlanId() != null) {
            MembershipPlan plan = planService.getPlanById(user.getCurrentPlanId());
            if (plan != null) {
                return ResponseEntity.ok(plan);
            }
        }
        return ResponseEntity.ok(Map.of("hasPlan", false));
    }

    // Get current user's payment history
    @GetMapping("/api/my-payments")
    @ResponseBody
    public ResponseEntity<List<Payment>> getMyPayments(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        int userId = (Integer) session.getAttribute("userId");
        return ResponseEntity.ok(paymentHistoryService.getPaymentsByUserId(userId));
    }

    @GetMapping("/monthprogress")
    public String monthProgressPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User user = guestService.getUserById(userId);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        return "monthprogress";
    }
}

