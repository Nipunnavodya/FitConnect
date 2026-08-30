package controller;

import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminMemberController {

    private final UserService userService;

    @Autowired
    public AdminMemberController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/members")
    public String membersPage(HttpServletRequest request, Model model) {
        int userId = (Integer) request.getSession().getAttribute("userId");
        User admin = userService.getUserById(userId);
        if (admin != null && admin.isAdmin()) {
            List<User> users = userService.getAllUsers();
            model.addAttribute("admin", admin);
            model.addAttribute("users", users);
            return "Admin_member_dashboard";
        }
        return "redirect:/login";
    }

    @PostMapping("/admin/api/add-user")
    @ResponseBody
    public Map<String, Object> addUser(@RequestParam String name, @RequestParam String email, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            user.setVerificationCode(0); // admin-created users are verified
            userService.registerUser(user);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @PostMapping("/admin/api/member-update-user/{userId}")
    @ResponseBody
    public Map<String, Object> updateUser(@PathVariable int userId, @RequestParam String name, @RequestParam String email, @RequestParam(required = false) String password) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.getUserById(userId);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            if (password != null && !password.isEmpty()) user.setPassword(password);
            userService.updateUser(user);
            response.put("success", true);
        } else {
            response.put("success", false);
            response.put("message", "User not found");
        }
        return response;
    }

    @PostMapping("/api/member-delete-user/{userId}")
    @ResponseBody
    public Map<String, Object> deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }

    @GetMapping("/api/users")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/api/total-members")
    @ResponseBody
    public int getTotalMembers() {
        return userService.getAllUsers().size();
    }

}
