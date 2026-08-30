package com.example.OOP_FitConnect.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.OOP_FitConnect.model.MembershipPlan;
import com.example.OOP_FitConnect.repository.DBController;

@Service
public class PlanService {

    @Autowired
    private DBController dbController;

    public List<MembershipPlan> getAllPlans() {
        return dbController.getAllPlans();
    }

    public MembershipPlan getPlanById(int id) {
        return dbController.getPlanById(id);
    }

    public MembershipPlan createPlan(MembershipPlan plan) {
        return dbController.savePlan(plan);
    }

    public MembershipPlan updatePlan(int id, MembershipPlan updatedPlan) {
        updatedPlan.setId(id);
        return dbController.updatePlan(updatedPlan);
    }

    public void deletePlan(int id) {
        dbController.deletePlan(id);
    }
}
