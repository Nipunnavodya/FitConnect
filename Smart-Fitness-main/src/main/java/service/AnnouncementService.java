package service;

import com.example.OOP_FitConnect.model.Announcement;
import com.example.OOP_FitConnect.repository.DBController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementService {

    private final DBController dbController;

    @Autowired
    public AnnouncementService(DBController dbController) {
        this.dbController = dbController;
    }

    public List<Announcement> getAllAnnouncements() {
        return dbController.getAllAnnouncements();
    }

    public List<Announcement> getActiveAnnouncements() {
        return dbController.getActiveAnnouncements();
    }

    public Announcement save(Announcement announcement) {
        return dbController.saveAnnouncement(announcement);
    }

    public void delete(Long id) {
        dbController.deleteAnnouncement(id);
    }

    public void toggle(Long id, boolean active) {
        dbController.toggleAnnouncement(id, active);
    }
}
