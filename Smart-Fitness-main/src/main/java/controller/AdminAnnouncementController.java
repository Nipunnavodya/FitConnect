package com.example.OOP_FitConnect.controller;

import com.example.OOP_FitConnect.model.Announcement;
import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.service.AnnouncementService;
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
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    private User requireAdmin(HttpServletRequest request) {
        Object id = request.getSession(false) != null ? request.getSession(false).getAttribute("userId") : null;
        if (id == null) return null;
        User u = userService.getUserById((Integer) id);
        return (u != null && u.isAdmin()) ? u : null;
    }

    @GetMapping("/announcements")
    public String announcementsPage(HttpServletRequest request, Model model) {
        User admin = requireAdmin(request);
        if (admin == null) return "redirect:/login";
        model.addAttribute("admin", admin);
        model.addAttribute("announcements", announcementService.getAllAnnouncements());
        return "admin_announcements";
    }

    @PostMapping("/announcements/add")
    @ResponseBody
    public Map<String, Object> addAnnouncement(
            @RequestParam String title,
            @RequestParam String content,
            HttpServletRequest request) {
        Map<String, Object> resp = new HashMap<>();
        User admin = requireAdmin(request);
        if (admin == null) {
            resp.put("success", false);
            resp.put("message", "Unauthorized");
            return resp;
        }
        try {
            Announcement a = new Announcement();
            a.setTitle(title);
            a.setContent(content);
            a.setPostedBy(admin.getName());
            announcementService.save(a);
            resp.put("success", true);
        } catch (Exception e) {
            resp.put("success", false);
            resp.put("message", e.getMessage());
        }
        return resp;
    }

    @PostMapping("/announcements/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteAnnouncement(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (requireAdmin(request) == null) {
            resp.put("success", false);
            resp.put("message", "Unauthorized");
            return resp;
        }
        announcementService.delete(id);
        resp.put("success", true);
        return resp;
    }

    @PostMapping("/announcements/toggle/{id}")
    @ResponseBody
    public Map<String, Object> toggleAnnouncement(
            @PathVariable Long id,
            @RequestParam boolean active,
            HttpServletRequest request) {
        Map<String, Object> resp = new HashMap<>();
        if (requireAdmin(request) == null) {
            resp.put("success", false);
            resp.put("message", "Unauthorized");
            return resp;
        }
        announcementService.toggle(id, active);
        resp.put("success", true);
        return resp;
    }
}
