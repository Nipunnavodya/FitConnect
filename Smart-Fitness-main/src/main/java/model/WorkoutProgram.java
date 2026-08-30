package com.example.OOP_FitConnect.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkoutProgram {

    private Long id;
    private String programName;
    private int instructorId;
    private Integer memberId;
    private String fitnessGoal;
    private String difficulty;      // beginner, intermediate, advanced
    private Integer durationWeeks;
    private Integer sessionsPerWeek;
    private Integer sessionDurationMin;
    private String description;
    private String status;          // ACTIVE, DRAFT
    private LocalDateTime createdAt;

    // Transient display fields
    private String instructorName;
    private String memberName;

    private List<WorkoutDay> days = new ArrayList<>();

    public WorkoutProgram() {
        this.status = "ACTIVE";
    }

    // ── Getters & Setters ──────────────────────────────────────────

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public String getProgramName()             { return programName; }
    public void setProgramName(String n)       { this.programName = n; }

    public int getInstructorId()               { return instructorId; }
    public void setInstructorId(int id)        { this.instructorId = id; }

    public Integer getMemberId()               { return memberId; }
    public void setMemberId(Integer memberId)  { this.memberId = memberId; }

    public String getFitnessGoal()             { return fitnessGoal; }
    public void setFitnessGoal(String g)       { this.fitnessGoal = g; }

    public String getDifficulty()              { return difficulty; }
    public void setDifficulty(String d)        { this.difficulty = d; }

    public Integer getDurationWeeks()          { return durationWeeks; }
    public void setDurationWeeks(Integer w)    { this.durationWeeks = w; }

    public Integer getSessionsPerWeek()        { return sessionsPerWeek; }
    public void setSessionsPerWeek(Integer s)  { this.sessionsPerWeek = s; }

    public Integer getSessionDurationMin()     { return sessionDurationMin; }
    public void setSessionDurationMin(Integer m) { this.sessionDurationMin = m; }

    public String getDescription()             { return description; }
    public void setDescription(String d)       { this.description = d; }

    public String getStatus()                  { return status; }
    public void setStatus(String status)       { this.status = status; }

    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime t)  { this.createdAt = t; }

    public String getInstructorName()              { return instructorName; }
    public void setInstructorName(String name)     { this.instructorName = name; }

    public String getMemberName()                  { return memberName; }
    public void setMemberName(String name)         { this.memberName = name; }

    public List<WorkoutDay> getDays()          { return days; }
    public void setDays(List<WorkoutDay> days) { this.days = days; }
}
