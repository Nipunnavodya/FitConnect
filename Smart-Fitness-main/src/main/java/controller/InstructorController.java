package com.example.OOP_FitConnect.controller;

import com.example.OOP_FitConnect.model.DietPlan;
import com.example.OOP_FitConnect.model.Meal;
import com.example.OOP_FitConnect.model.User;
import com.example.OOP_FitConnect.model.WorkoutDay;
import com.example.OOP_FitConnect.model.WorkoutExercise;
import com.example.OOP_FitConnect.model.WorkoutProgram;
import com.example.OOP_FitConnect.service.GuestService;
import com.example.OOP_FitConnect.service.InstructorDietPlanService;
import com.example.OOP_FitConnect.service.InstructorWorkoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/instructor")
public class InstructorController {

    @Autowired
    private GuestService guestService;

    @Autowired
    private InstructorDietPlanService dietPlanService;

    @Autowired
    private InstructorWorkoutService workoutService;

    // ── Auth guard helper ──────────────────────────────────────────

    /** Returns the logged-in instructor, or null if not authenticated as instructor. */
    private User getInstructor(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) return null;
        int userId = (Integer) session.getAttribute("userId");
        User user = guestService.getUserById(userId);
        if (user == null) return null;
        String role = user.getRole();
        return ("INSTRUCTOR".equals(role) || "ADMIN".equals(role)) ? user : null;
    }

    // ── Dashboard ──────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";

        model.addAttribute("instructor", instructor);
        model.addAttribute("totalMembers",  guestService.getAllMembers().size());
        model.addAttribute("totalDietPlans", dietPlanService.countByInstructor(instructor.getId()));
        model.addAttribute("totalWorkouts",  workoutService.countByInstructor(instructor.getId()));
        model.addAttribute("sessionsToday",  0); // placeholder
        model.addAttribute("recentDietPlans",
                limitList(dietPlanService.getPlansByInstructor(instructor.getId()), 3));
        model.addAttribute("recentWorkouts",
                limitList(workoutService.getProgramsByInstructor(instructor.getId()), 3));
        return "instructor_dashboard";
    }

    // ── Create Diet Plan ───────────────────────────────────────────

    @GetMapping("/create-diet-plan")
    public String createDietPlanForm(HttpServletRequest request, Model model) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";

        model.addAttribute("instructor", instructor);
        model.addAttribute("dietPlan", new DietPlan());
        model.addAttribute("members", guestService.getAllMembers());
        return "instructor_create_diet_plan";
    }

    @PostMapping("/create-diet-plan")
    public String saveDietPlan(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";

        DietPlan plan = new DietPlan();
        plan.setInstructorId(instructor.getId());
        plan.setPlanName(param(request, "planName", "Untitled Plan"));
        plan.setGoal(param(request, "goal", null));
        plan.setNotes(param(request, "notes", null));
        plan.setStatus("ACTIVE");

        String memberIdStr = param(request, "memberId", null);
        if (memberIdStr != null && !memberIdStr.isBlank()) {
            try { plan.setMemberId(Integer.parseInt(memberIdStr)); } catch (NumberFormatException ignored) {}
        }

        plan.setDailyCalories(parseNullableInt(param(request, "dailyCalories", null)));
        plan.setDurationWeeks(parseNullableInt(param(request, "durationWeeks", null)));
        plan.setProteinGrams(parseNullableInt(param(request, "proteinGrams", null)));
        plan.setCarbsGrams(parseNullableInt(param(request, "carbsGrams", null)));
        plan.setFatGrams(parseNullableInt(param(request, "fatGrams", null)));

        // Parse meals[0].mealType, meals[1].mealType, ...
        List<Meal> meals = new ArrayList<>();
        for (int i = 0; ; i++) {
            String mealType = request.getParameter("meals[" + i + "].mealType");
            if (mealType == null) break;
            Meal meal = new Meal();
            meal.setMealType(mealType);
            meal.setFoodItems(request.getParameter("meals[" + i + "].foodItems"));
            meal.setCalories(parseNullableInt(request.getParameter("meals[" + i + "].calories")));
            meals.add(meal);
        }
        plan.setMeals(meals);

        dietPlanService.createPlan(plan);
        redirectAttributes.addFlashAttribute("success", "Diet plan saved successfully!");
        return "redirect:/instructor/dashboard";
    }

    // ── Create Workout Program ─────────────────────────────────────

    @GetMapping("/create-workout")
    public String createWorkoutForm(HttpServletRequest request, Model model) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";

        model.addAttribute("instructor", instructor);
        model.addAttribute("workoutProgram", new WorkoutProgram());
        model.addAttribute("members", guestService.getAllMembers());
        return "instructor_create_workout";
    }

    @PostMapping("/create-workout")
    public String saveWorkout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";

        WorkoutProgram program = new WorkoutProgram();
        program.setInstructorId(instructor.getId());
        program.setProgramName(param(request, "programName", "Untitled Program"));
        program.setFitnessGoal(param(request, "fitnessGoal", null));
        program.setDifficulty(param(request, "difficulty", null));
        program.setDescription(param(request, "description", null));
        program.setStatus("ACTIVE");

        String memberIdStr = param(request, "memberId", null);
        if (memberIdStr != null && !memberIdStr.isBlank()) {
            try { program.setMemberId(Integer.parseInt(memberIdStr)); } catch (NumberFormatException ignored) {}
        }

        program.setDurationWeeks(parseNullableInt(param(request, "durationWeeks", null)));
        program.setSessionsPerWeek(parseNullableInt(param(request, "sessionsPerWeek", null)));
        program.setSessionDurationMin(parseNullableInt(param(request, "sessionDurationMin", null)));

        // Parse days[0].dayName, days[0].focus, days[0].exercises[0].name, ...
        List<WorkoutDay> days = new ArrayList<>();
        for (int d = 0; ; d++) {
            String dayName = request.getParameter("days[" + d + "].dayName");
            if (dayName == null) break;

            WorkoutDay day = new WorkoutDay();
            day.setDayName(dayName);
            day.setFocus(request.getParameter("days[" + d + "].focus"));

            List<WorkoutExercise> exercises = new ArrayList<>();
            for (int e = 0; ; e++) {
                String exName = request.getParameter("days[" + d + "].exercises[" + e + "].name");
                if (exName == null) break;
                WorkoutExercise ex = new WorkoutExercise();
                ex.setName(exName);
                ex.setSets(parseNullableInt(request.getParameter("days[" + d + "].exercises[" + e + "].sets")));
                ex.setReps(request.getParameter("days[" + d + "].exercises[" + e + "].reps"));
                ex.setRestSeconds(parseNullableInt(request.getParameter("days[" + d + "].exercises[" + e + "].restSeconds")));
                exercises.add(ex);
            }
            day.setExercises(exercises);
            days.add(day);
        }
        program.setDays(days);

        workoutService.createProgram(program);
        redirectAttributes.addFlashAttribute("success", "Workout program saved successfully!");
        return "redirect:/instructor/dashboard";
    }

    // ── My Diet Plans ──────────────────────────────────────────────

    @GetMapping("/diet-plans")
    public String dietPlans(HttpServletRequest request, Model model) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";
        model.addAttribute("instructor", instructor);
        model.addAttribute("dietPlans", dietPlanService.getPlansByInstructor(instructor.getId()));
        return "instructor_diet_plans";
    }

    @PostMapping("/delete-diet-plan")
    public String deleteDietPlan(HttpServletRequest request,
                                 @RequestParam Long planId,
                                 RedirectAttributes redirectAttributes) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";
        dietPlanService.deletePlan(planId);
        redirectAttributes.addFlashAttribute("success", "Diet plan deleted successfully.");
        return "redirect:/instructor/diet-plans";
    }

    // ── My Workout Programs ────────────────────────────────────────

    @GetMapping("/workouts")
    public String workouts(HttpServletRequest request, Model model) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";
        model.addAttribute("instructor", instructor);
        model.addAttribute("workoutPrograms", workoutService.getProgramsByInstructor(instructor.getId()));
        return "instructor_workouts";
    }

    @PostMapping("/delete-workout")
    public String deleteWorkout(HttpServletRequest request,
                                @RequestParam Long programId,
                                RedirectAttributes redirectAttributes) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";
        workoutService.deleteProgram(programId);
        redirectAttributes.addFlashAttribute("success", "Workout program deleted successfully.");
        return "redirect:/instructor/workouts";
    }

    // ── Other instructor pages ─────────────────────────────────────

    @GetMapping("/members")
    public String members(HttpServletRequest request, Model model) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";
        model.addAttribute("instructor", instructor);
        model.addAttribute("members", guestService.getAllMembers());
        return "instructor_dashboard"; // reuse dashboard for now; create dedicated page later
    }

    @GetMapping("/schedule")
    public String schedule(HttpServletRequest request, Model model) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";
        model.addAttribute("instructor", instructor);
        return "schedule"; // reuse member schedule template
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";
        model.addAttribute("instructor", instructor);
        model.addAttribute("user", instructor); // profile template uses 'user'
        return "profile";
    }

    @GetMapping("/settings")
    public String settings(HttpServletRequest request, Model model) {
        User instructor = getInstructor(request);
        if (instructor == null) return "redirect:/login";
        model.addAttribute("instructor", instructor);
        model.addAttribute("user", instructor);
        return "User-Settings";
    }

    // ── Helpers ────────────────────────────────────────────────────

    private String param(HttpServletRequest req, String name, String defaultValue) {
        String val = req.getParameter(name);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }

    private Integer parseNullableInt(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Integer.parseInt(value.trim()); } catch (NumberFormatException e) { return null; }
    }

    private <T> List<T> limitList(List<T> list, int max) {
        return list.size() <= max ? list : list.subList(0, max);
    }
}
