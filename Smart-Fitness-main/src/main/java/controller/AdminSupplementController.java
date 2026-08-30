package controller;

import com.example.OOP_FitConnect.model.Supplement;
import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.service.GuestService;
import com.example.OOP_FitConnect.service.SupplementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin/supplements")
public class AdminSupplementController {

    private final SupplementService supplementService;
    private final GuestService guestService;

    @Autowired
    public AdminSupplementController(SupplementService supplementService, GuestService guestService) {
        this.supplementService = supplementService;
        this.guestService = guestService;
    }

    @GetMapping("/add")
    public String showAddSupplementForm(HttpServletRequest request, Model model) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }
        model.addAttribute("supplement", new Supplement());
        model.addAttribute("supplements", supplementService.getAllSupplements());
        return "admin_add_supplement";
    }

    @PostMapping("/add")
    public String addSupplement(@RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("category") String category,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest request,
            Model model) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }

        Supplement supplement;
        if (id != null) {
            supplement = supplementService.getSupplementById(id);
        } else {
            supplement = new Supplement();
        }

        supplement.setName(name);
        supplement.setDescription(description);
        supplement.setPrice(price);
        supplement.setCategory(category);

        try {
            supplementService.addSupplement(supplement, image);
            model.addAttribute("message",
                    id != null ? "Supplement updated successfully!" : "Supplement added successfully!");
        } catch (IOException e) {
            model.addAttribute("error", "Failed to process supplement: " + e.getMessage());
            model.addAttribute("supplement", supplement);
            model.addAttribute("supplements", supplementService.getAllSupplements());
            return "admin_add_supplement";
        }

        return "redirect:/admin/supplements/add";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }
        Supplement supplement = supplementService.getSupplementById(id);
        model.addAttribute("supplement", supplement);
        model.addAttribute("supplements", supplementService.getAllSupplements());
        return "admin_add_supplement";
    }

    @PostMapping("/delete/{id}")
    public String deleteSupplement(@PathVariable("id") Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }
        supplementService.deleteSupplement(id);
        return "redirect:/admin/supplements/add";
    }

    private boolean isAdmin(HttpServletRequest request) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null)
            return false;
        User user = guestService.getUserById(userId);
        return user != null && user.isAdmin();
    }
}
