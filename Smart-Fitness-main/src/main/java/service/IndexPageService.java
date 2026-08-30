package service;

import com.example.OOP_FitConnect.model.IndexPage;
import com.example.OOP_FitConnect.repository.IndexPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexPageService {

    private final IndexPageRepository indexPageRepository;

    @Autowired
    public IndexPageService(IndexPageRepository indexPageRepository) {
        this.indexPageRepository = indexPageRepository;
    }

    public IndexPage getWelcomeMessage() {
        return indexPageRepository.getWelcomeMessage();
    }
}
