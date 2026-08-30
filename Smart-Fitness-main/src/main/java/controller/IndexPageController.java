package controller;

import com.example.OOP_FitConnect.model.IndexPage;
import com.example.OOP_FitConnect.service.IndexPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexPageController {

    private final IndexPageService indexPageService;

    @Autowired
    public IndexPageController(IndexPageService indexPageService) {
        this.indexPageService = indexPageService;
    }

    @GetMapping("/")
    public String viewIndex(Model model) {
        IndexPage indexPage = indexPageService.getWelcomeMessage();
        model.addAttribute("welcomeMessage", indexPage.getWelcomeMessage());
        return "index"; // Assuming your HTML file is named index.html
    }
}
