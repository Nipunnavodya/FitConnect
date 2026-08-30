package controller;

import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.service.AdminService;
import com.example.OOP_FitConnect.service.GuestService;
import com.example.OOP_FitConnect.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private GuestService guestService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String adminDashboard(HttpServletRequest request, Model model) {
        int userId = (Integer) request.getSession().getAttribute("userId");
        User admin = guestService.getUserById(userId);

        if (admin != null && admin.isAdmin()) {
            List<User> users = guestService.getAllUsers();
            Map<String, Object> stats = adminService.getDashboardStats();

            // Add all required attributes with default values
            model.addAttribute("admin", admin);
            model.addAttribute("users", users);
            model.addAttribute("userStats", stats.get("userStats"));
            model.addAttribute("workoutStats", stats.get("workoutStats"));
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages", 1);
            model.addAttribute("createdAt", java.time.LocalDateTime.now());

            return "admin_dashboard";
        }

        return "redirect:login";
    }

    @PostMapping("/api/create-admin")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createAdmin(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        int adminId = (Integer) request.getSession().getAttribute("userId");
        User currentAdmin = guestService.getUserById(adminId);

        if (currentAdmin != null && currentAdmin.isAdmin()) {
            if (guestService.getUserByEmail(email) != null) {
                response.put("success", false);
                response.put("message", "Email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            User newAdmin = adminService.createAdmin(name, email, password);
            response.put("success", true);
            response.put("message", "Admin created successfully");
            return ResponseEntity.ok(response);
        }

        response.put("success", false);
        response.put("message", "Unauthorized access");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/api/update-user/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable int userId,
            @RequestParam String name,
            @RequestParam String email,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        int adminId = (Integer) request.getSession().getAttribute("userId");
        User admin = guestService.getUserById(adminId);

        if (admin != null && admin.isAdmin()) {
            User updatedUser = adminService.updateUser(userId, name, email);
            if (updatedUser != null) {
                response.put("success", true);
                response.put("message", "User updated successfully");
                return ResponseEntity.ok(response);
            }
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", false);
        response.put("message", "Unauthorized access");
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/api/statistics")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getStatistics() {
        return ResponseEntity.ok(adminService.getUserStatistics());
    }

    @PostMapping("/api/delete-user/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable int userId,
            HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        int adminId = (Integer) request.getSession().getAttribute("userId");
        User admin = guestService.getUserById(adminId);

        if (admin != null && admin.isAdmin()) {
            User userToDelete = guestService.getUserById(userId);

            if (userToDelete != null && !userToDelete.isAdmin()) {
                guestService.deleteUser(userId);

                response.put("success", true);
                response.put("message", "User deleted successfully");
                return ResponseEntity.ok(response);
            }

            response.put("success", false);
            response.put("message", "User not found or cannot delete admin");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("success", false);
        response.put("message", "Unauthorized access");
        return ResponseEntity.badRequest().body(response);
    }
    @GetMapping("/admin/profile")
    public String profilePage(HttpServletRequest request, Model model) {
        int userId = (Integer) request.getSession().getAttribute("userId");
        User admin = userService.getUserById(userId);
        if (admin != null && admin.isAdmin()) {
            model.addAttribute("admin", admin);
            return "Admin_Profile";
        }
        return "redirect:/login";
    }
     @GetMapping("/admin/logout")
    public String showLogoutConfirmation() {
        return "Admin_logout";
    }

    @GetMapping("/admin/logout/confirm")
    public String confirmLogout() {
        return "redirect:/";
    }
}
