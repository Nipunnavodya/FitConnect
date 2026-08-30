package com.example.OOP_FitConnect.controller;

import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminInstructorController {

    @Autowired
    private UserService userService;

    private User requireAdmin(HttpServletRequest request) {
        Object id = request.getSession(false) != null ? request.getSession(false).getAttribute("userId") : null;
        if (id == null) return null;
        User u = userService.getUserById((Integer) id);
        return (u != null && u.isAdmin()) ? u : null;
    }

    @GetMapping("/instructors")
    public String instructorsPage(HttpServletRequest request, Model model) {
        User admin = requireAdmin(request);
        if (admin == null) return "redirect:/login";
        model.addAttribute("admin", admin);
        model.addAttribute("instructors", userService.getAllInstructors());
        return "admin_instructors";
    }

    @PostMapping("/api/add-instructor")
    @ResponseBody
    public Map<String, Object> addInstructor(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String branch,
            HttpServletRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (requireAdmin(request) == null) {
            resp.put("success", false);
            resp.put("message", "Unauthorized");
            return resp;
        }
        try {
            User instructor = new User();
            instructor.setName(name);
            instructor.setEmail(email);
            instructor.setPassword(password);
            instructor.setBranch(branch);
            instructor.setRole("INSTRUCTOR");
            instructor.setVerificationCode(0);
            userService.registerUser(instructor);
            resp.put("success", true);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
        }
        return resp;
    }

    @PostMapping("/api/update-instructor/{instructorId}")
    @ResponseBody
    public Map<String, Object> updateInstructor(
            @PathVariable int instructorId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String branch,
            HttpServletRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (requireAdmin(request) == null) {
            resp.put("success", false);
            resp.put("message", "Unauthorized");
            return resp;
        }
        User instructor = userService.getUserById(instructorId);
        if (instructor == null || !"INSTRUCTOR".equals(instructor.getRole())) {
            resp.put("success", false);
            resp.put("message", "Instructor not found");
            return resp;
        }
        instructor.setName(name);
        instructor.setEmail(email);
        if (password != null && !password.isEmpty()) instructor.setPassword(password);
        if (branch != null) instructor.setBranch(branch);
        userService.updateUser(instructor);
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/api/delete-instructor/{instructorId}")
    @ResponseBody
    public Map<String, Object> deleteInstructor(@PathVariable int instructorId, HttpServletRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (requireAdmin(request) == null) {
            resp.put("success", false);
            resp.put("message", "Unauthorized");
            return resp;
        }
        userService.deleteUser(instructorId);
        resp.put("success", true);
        return resp;
    }
}
