package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.repository.DBController;

@Service
public class AdminService {

    @Autowired
    private DBController dbController;
    @Autowired
    private GuestService guestService;

    public Map<String, Object> getDashboardStats() {
        List<User> users = guestService.getAllUsers();
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = users.size();
        long verifiedUsers = users.stream().filter(User::isVerified).count();

        Map<String, Object> userStats = new HashMap<>();
        userStats.put("total", totalUsers);
        userStats.put("verified", verifiedUsers);

        stats.put("userStats", userStats);

        return stats;
    }

    public User createAdmin(String name, String email, String password) {
        User admin = new User();
        admin.setName(name);
        admin.setEmail(email);
        admin.setPassword(password);
        admin.setVerificationCode(0); // verified
        return dbController.saveUser(admin);
    }

    public User updateUser(int userId, String name, String email) {
        User user = guestService.getUserById(userId);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            return dbController.updateUser(user);
        }
        return null;
    }

    public Map<String, Long> getUserStatistics() {
        List<User> users = guestService.getAllUsers();
        Map<String, Long> stats = new HashMap<>();

        stats.put("totalUsers", (long) users.size());
        stats.put("verifiedUsers", users.stream().filter(User::isVerified).count());
        stats.put("adminUsers", users.stream().filter(User::isAdmin).count());
        stats.put("regularUsers", users.stream().filter(u -> !u.isAdmin()).count());

        return stats;
    }
}
