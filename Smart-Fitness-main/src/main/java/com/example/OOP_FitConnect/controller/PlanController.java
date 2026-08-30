package com.example.OOP_FitConnect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OOP_FitConnect.model.MembershipPlan;
import com.example.OOP_FitConnect.service.PlanService;

@RestController
@RequestMapping("/admin/api/plans")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping
    public List<MembershipPlan> getAllPlans() {
        return planService.getAllPlans();
    }

    @GetMapping("/{id}")
    public MembershipPlan getPlanById(@PathVariable int id) {
        return planService.getPlanById(id);
    }

    @PostMapping
    public MembershipPlan createPlan(@RequestBody MembershipPlan plan) {
        return planService.createPlan(plan);
    }

    @PutMapping("/{id}")
    public MembershipPlan updatePlan(@PathVariable int id, @RequestBody MembershipPlan plan) {
        return planService.updatePlan(id, plan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable int id) {
        planService.deletePlan(id);
        return ResponseEntity.ok().build();
    }
}
