package repository;

import com.example.OOP_FitConnect.model.IndexPage;
import org.springframework.stereotype.Repository;

@Repository
public class IndexPageRepository {

    public IndexPage getWelcomeMessage() {
        return new IndexPage("Welcome to Smart Fitness Gym!");
    }
}
