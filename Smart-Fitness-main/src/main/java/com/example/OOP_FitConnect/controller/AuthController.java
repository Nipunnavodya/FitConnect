package com.example.OOP_FitConnect.controller;

import java.util.HashMap;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.OOP_FitConnect.model.MembershipPlan;
import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.service.GuestService;
import com.example.OOP_FitConnect.service.InstructorDietPlanService;
import com.example.OOP_FitConnect.service.InstructorWorkoutService;
import com.example.OOP_FitConnect.service.PlanService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired
    private GuestService guestService;

    @Autowired
    private PlanService planService;

    @Autowired
    private InstructorDietPlanService dietPlanService;

    @Autowired
    private InstructorWorkoutService workoutService;

    // ── Guest-accessible pages ─────────────────────────────────────

    @GetMapping("/index")
    public String indexPage() { return "index"; }

    @GetMapping("/gallery")
    public String galleryPage() { return "gallery"; }

    @GetMapping("/memplan")
    public String memplanPage() { return "memplan"; }

    @GetMapping("/UserPlans")
    public String userPlansPage() { return "UserPlans"; }

    @GetMapping("/About_us")
    public String aboutUsPage() { return "About_us"; }

    // ── Auth pages ─────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() { return "forgot-password"; }

    @GetMapping("/verification-result")
    public String verificationResultPage() { return "verification-result"; }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam int code, Model model) {
        if (guestService.isValidResetCode(code)) {
            model.addAttribute("code", code);
            return "reset-password";
        }
        return "redirect:/login?error=invalid_code";
    }

    // ── Member dashboard ───────────────────────────────────────────

    @GetMapping("/user/dashboard")
    public String userDashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User user = guestService.getUserById(userId);
            if (user != null && "USER".equals(user.getRole())) {
                model.addAttribute("user", user);
                // Inject assigned plans for the dashboard widgets
                model.addAttribute("assignedDietPlans",
                        dietPlanService.getPlansByMember(userId));
                model.addAttribute("assignedWorkoutPrograms",
                        workoutService.getProgramsByMember(userId));
                return "member_dashboard";
            }
        }
        return "redirect:/login";
    }

    // ── Admin dashboard ────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String adminDashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User admin = guestService.getUserById(userId);
            if (admin != null && "ADMIN".equals(admin.getRole())) {
                model.addAttribute("admin", admin);
                return "admin_dashboard";
            }
        }
        return "redirect:/login";
    }

    // ── Member sub-pages ───────────────────────────────────────────

    @GetMapping("/user/profile")
    public String userProfile(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User user = guestService.getUserById(userId);
            if (user != null && "USER".equals(user.getRole())) {
                if (user.getCurrentPlanId() != null) {
                    MembershipPlan plan = planService.getPlanById(user.getCurrentPlanId());
                    if (plan != null) user.setCurrentPlanName(plan.getName());
                }
                model.addAttribute("user", user);
                return "profile";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/user/schedule")
    public String userSchedule(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User user = guestService.getUserById(userId);
            if (user != null && "USER".equals(user.getRole())) {
                model.addAttribute("user", user);
                model.addAttribute("assignedWorkoutPrograms", workoutService.getProgramsByMember(userId));
                return "schedule";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/user/settings")
    public String userSettings(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");
            User user = guestService.getUserById(userId);
            if (user != null && "USER".equals(user.getRole())) {
                model.addAttribute("user", user);
                return "User-Settings";
            }
        }
        return "redirect:/login";
    }

    // ── Login ──────────────────────────────────────────────────────

    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        User user = guestService.authenticate(email, password);
        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole());

            response.put("success", true);
            String redirectUrl;
            switch (user.getRole()) {
                case "ADMIN"       -> redirectUrl = "/admin/dashboard";
                case "INSTRUCTOR"  -> redirectUrl = "/instructor/dashboard";
                default            -> redirectUrl = "/user/dashboard";
            }
            response.put("redirect", redirectUrl);
            if (!user.isVerified() && !"ADMIN".equals(user.getRole())) {
                response.put("warning", "Please verify your email to access all features");
            }
            return ResponseEntity.ok(response);
        }

        response.put("success", false);
        response.put("message", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // ── Register ───────────────────────────────────────────────────

    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String branch,
            @RequestParam(required = false, defaultValue = "USER") String role,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        if (guestService.getUserByEmail(email) != null) {
            response.put("success", false);
            response.put("message", "Email already registered");
            return ResponseEntity.badRequest().body(response);
        }

        int verificationCode = guestService.generateVerificationCode();
        User user = guestService.registerUser(name, email, password, verificationCode, branch, role);
        guestService.sendVerificationEmail(user, verificationCode);

        response.put("success", true);
        response.put("message", "Registration successful! Please check your email to verify your account.");
        return ResponseEntity.ok(response);
    }

    // ── Forgot / Reset password ────────────────────────────────────

    @PostMapping("/api/forgot-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        User user = guestService.getUserByEmail(email);
        if (user != null) {
            int resetCode = guestService.generatePasswordResetCode(user);
            guestService.sendPasswordResetEmail(user, resetCode);
            response.put("success", true);
            response.put("message", "Password reset instructions sent to your email");
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("message", "Email not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping("/api/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestParam int code,
            @RequestParam String password) {

        Map<String, Object> response = new HashMap<>();
        if (guestService.resetPassword(code, password)) {
            response.put("success", true);
            response.put("message", "Password reset successful");
            response.put("redirect", "/login");
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("message", "Invalid or expired code");
        return ResponseEntity.badRequest().body(response);
    }

    // ── Logout ─────────────────────────────────────────────────────

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        redirectAttributes.addFlashAttribute("message", "Logged out successfully.");
        return "redirect:/login";
    }
}
