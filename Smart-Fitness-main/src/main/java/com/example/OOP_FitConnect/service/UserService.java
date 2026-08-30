package com.example.OOP_FitConnect.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.repository.DBController;

@Service

public class UserService {
    private final DBController dbController;

    @Autowired
    public UserService(DBController dbController) {
        this.dbController = dbController;
    }

    public User getUserById(int id) {
        return dbController.getUserById(id);
    }
    public List<User> getAllUsers() {
        return dbController.getAllUsers();
    }

    public User registerUser(User user) {
        return dbController.saveUser(user);
    }

    public User updateUser(User user) {
        return dbController.updateUser(user);
    }

    public void deleteUser(int id) {
        dbController.deleteUser(id);
    }

    public List<User> getAllMembers() {
        return dbController.getAllMembers();
    }

    public List<User> getAllInstructors() {
        return dbController.getAllInstructors();
    }
    // Add more user-related methods as needed
}
