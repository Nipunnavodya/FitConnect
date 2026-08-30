package com.example.OOP_FitConnect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.service.GuestService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ExerciseController {

    @Autowired
    private GuestService guestService;

    @GetMapping("/exercises")
    public String showExercisesPage(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User user = guestService.getUserById(userId);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
        return "exercises";
    }

    @GetMapping("/exercises/boxing-rings")
    public String showBoxingRingsDetails() {
        return "Boxing rings Details";
    }

    @GetMapping("/exercises/crossfit")
    public String showCrossFitDetails() {
        return "CrossFit Details";
    }

    @GetMapping("/exercises/cycling")
    public String showCyclingDetails() {
        return "Cycling Details";
    }

    @GetMapping("/exercises/flexibility-yoga")
    public String showFlexibilityYogaDetails() {
        return "Flexibility & Yoga Details";
    }

    @GetMapping("/exercises/featured")
    public String showFeaturedExercises() {
        return "Featured Exercises";
    }

    @GetMapping("/exercises/hiit-workouts")
    public String showHIITWorkoutsDetails() {
        return "HIIT Workouts Details";
    }

    @GetMapping("/exercises/popular-workouts")
    public String showPopularWorkouts() {
        return "Popular Workouts";
    }

    @GetMapping("/exercises/strength-training")
    public String showStrengthTrainingDetails() {
        return "Strength Training Details";
    }

    @GetMapping("/exercises/popular-categories")
    public String showPopularCategories() {
        return "popular-categories";
    }
}
