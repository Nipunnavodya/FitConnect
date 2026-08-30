package controller;

import com.example.OOP_FitConnect.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/contact")
    public ResponseEntity<Map<String, Object>> handleContactForm(@RequestBody Map<String, String> formData) {
        try {
            contactService.saveContactForm(formData);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Message sent successfully!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to send message: " + e.getMessage()
            ));
        }
    }
}
