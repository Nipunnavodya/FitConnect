package com.example.OOP_FitConnect.model;

public class WorkoutExercise {

    private Long id;
    private Long dayId;
    private String name;
    private Integer sets;
    private String reps;          // "12 reps" or "30s"
    private Integer restSeconds;
    private int exerciseOrder;

    public WorkoutExercise() {}

    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public Long getDayId()                 { return dayId; }
    public void setDayId(Long dayId)       { this.dayId = dayId; }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }

    public Integer getSets()               { return sets; }
    public void setSets(Integer sets)      { this.sets = sets; }

    public String getReps()                { return reps; }
    public void setReps(String reps)       { this.reps = reps; }

    public Integer getRestSeconds()            { return restSeconds; }
    public void setRestSeconds(Integer rs)     { this.restSeconds = rs; }

    public int getExerciseOrder()              { return exerciseOrder; }
    public void setExerciseOrder(int order)    { this.exerciseOrder = order; }
}
