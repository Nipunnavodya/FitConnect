package com.example.OOP_FitConnect.service;

import com.example.OOP_FitConnect.model.WorkoutProgram;
import com.example.OOP_FitConnect.repository.InstructorWorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorWorkoutService {

    @Autowired
    private InstructorWorkoutRepository repo;

    public WorkoutProgram createProgram(WorkoutProgram program) {
        return repo.save(program);
    }

    public void deleteProgram(Long id) {
        repo.delete(id);
    }

    public List<WorkoutProgram> getProgramsByInstructor(int instructorId) {
        return repo.findByInstructorId(instructorId);
    }

    public List<WorkoutProgram> getProgramsByMember(int memberId) {
        return repo.findByMemberId(memberId);
    }

    public List<WorkoutProgram> getAllPrograms() {
        return repo.findAll();
    }

    public int countByInstructor(int instructorId) {
        return repo.countByInstructorId(instructorId);
    }
}
