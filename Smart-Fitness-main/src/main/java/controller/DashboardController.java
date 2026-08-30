package com.example.OOP_FitConnect.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.model.WorkoutPlan;
import com.example.OOP_FitConnect.service.GuestService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    @Autowired
    private GuestService guestService;

    @GetMapping("/member_dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User user = guestService.getUserById(userId);

            if (user != null) {
                model.addAttribute("user", user);

                List<WorkoutPlan> workoutPlans = guestService.getUserWorkoutPlans(userId);
                model.addAttribute("workoutPlans", workoutPlans);

                int totalWorkouts = workoutPlans.size();
                int completedWorkouts = (int) workoutPlans.stream()
                        .filter(WorkoutPlan::isCompleted)
                        .count();

                double completionRate = totalWorkouts > 0
                        ? (double) completedWorkouts / totalWorkouts * 100
                        : 0;

                model.addAttribute("totalWorkouts", totalWorkouts);
                model.addAttribute("completedWorkouts", completedWorkouts);
                model.addAttribute("completionRate", Math.round(completionRate));

                return "member_dashboard";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/api/add-workout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addWorkout(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String schedule,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User user = guestService.getUserById(userId);

            if (user != null && !user.isGuest()) {
                WorkoutPlan workoutPlan = new WorkoutPlan();
                workoutPlan.setName(name);
                workoutPlan.setDescription(description);
                workoutPlan.setSchedule(schedule);
                workoutPlan.setCompleted(false);

                guestService.addWorkoutPlan(userId, workoutPlan);

                response.put("success", true);
                response.put("message", "Workout plan added successfully");
                response.put("workoutPlan", workoutPlan);
                return ResponseEntity.ok(response);
            } else if (user != null && user.isGuest()) {
                response.put("success", false);
                response.put("message", "Guests cannot add workouts. Please register.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }
        response.put("success", false);
        response.put("message", "Not authenticated");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/api/complete-workout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> completeWorkout(
            @RequestParam String workoutId,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User user = guestService.getUserById(userId);

            if (user != null && !user.isGuest()) {
                boolean updated = guestService.completeWorkout(userId, workoutId);
                if (updated) {
                    response.put("success", true);
                    response.put("message", "Workout marked as completed");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("success", false);
                    response.put("message", "Workout not found");
                    return ResponseEntity.badRequest().body(response);
                }
            } else if (user != null && user.isGuest()) {
                response.put("success", false);
                response.put("message", "Guests cannot complete workouts. Please register.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }

        response.put("success", false);
        response.put("message", "Not authenticated");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }
}
