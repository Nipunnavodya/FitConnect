package controller;

import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.service.AdminService;
import com.example.OOP_FitConnect.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminReportController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    @GetMapping("/admin/report")
    public String reportPage(HttpServletRequest request, Model model) {
        int userId = (Integer) request.getSession().getAttribute("userId");
        User admin = userService.getUserById(userId);
        if (admin != null && admin.isAdmin()) {
            Map<String, Object> stats = adminService.getDashboardStats();
            List<User> users = userService.getAllUsers();
            model.addAttribute("admin", admin);
            model.addAttribute("userStats", stats.get("userStats"));
            model.addAttribute("workoutStats", stats.get("workoutStats"));
            model.addAttribute("users", users);
            return "Admin_Report";
        }
        return "redirect:/login";
    }
}
