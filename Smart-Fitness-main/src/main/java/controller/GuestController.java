package controller;

import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.service.GuestService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guest")  //  Added a base path for guest-related URLs
public class GuestController {

    @Autowired
    private GuestService guestService;

    //  Moved guest accessible pages from AuthController to here.
    @GetMapping("/")
    public String indexPage() {
        return "index";
    }

    @GetMapping("/gallery")
    public String galleryPage() {
        return "gallery";
    }

    @GetMapping("/memplan")
    public String memplanPage() {
        return "memplan";
    }

    @GetMapping("/About_us")
    public String aboutUsPage() {
        return "About_us";
    }

    @GetMapping("/supplements")
    public String supplementsPage() {
        return "supplements";
    }

    @GetMapping("/dashboard")
    public String guestDashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        User guestUser = null;

        if (session.getAttribute("userId") == null) {
            // Create a new guest user (transient, not saved to DB)
            guestUser = guestService.createGuestUser();
            session.setAttribute("userId", guestUser.getId());      //encapsulation
            session.setAttribute("userRole", "GUEST"); //important
            model.addAttribute("user", guestUser);  //send user to the page
        }
        else{
            int userId = (Integer) session.getAttribute("userId");
            guestUser = guestService.getUserById(userId);
            model.addAttribute("user", guestUser);
        }

        //  Return the guest dashboard
        return "guest_dashboard";
    }

    @PostMapping("/api/start-guest-session")
    public String startGuestSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User guestUser = guestService.createGuestUser();  // Create a transient guest user.
        session.setAttribute("userId", guestUser.getId());  // Store the user's ID in the session., encapsulation
        session.setAttribute("userRole", "GUEST");
        return "redirect:/guest/dashboard"; // Redirect to the guest dashboard.
    }

}
