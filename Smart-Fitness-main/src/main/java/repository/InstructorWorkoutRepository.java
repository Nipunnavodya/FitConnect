package com.example.OOP_FitConnect.repository;

import com.example.OOP_FitConnect.model.WorkoutDay;
import com.example.OOP_FitConnect.model.WorkoutExercise;
import com.example.OOP_FitConnect.model.WorkoutProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class InstructorWorkoutRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<WorkoutProgram> programMapper = (rs, rowNum) -> {
        WorkoutProgram p = new WorkoutProgram();
        p.setId(rs.getLong("id"));
        p.setProgramName(rs.getString("program_name"));
        p.setInstructorId(rs.getInt("instructor_id"));
        int mid = rs.getInt("member_id");
        p.setMemberId(rs.wasNull() ? null : mid);
        p.setFitnessGoal(rs.getString("fitness_goal"));
        p.setDifficulty(rs.getString("difficulty"));
        int dur = rs.getInt("duration_weeks");
        p.setDurationWeeks(rs.wasNull() ? null : dur);
        int spw = rs.getInt("sessions_per_week");
        p.setSessionsPerWeek(rs.wasNull() ? null : spw);
        int sdm = rs.getInt("session_duration_min");
        p.setSessionDurationMin(rs.wasNull() ? null : sdm);
        p.setDescription(rs.getString("description"));
        p.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) p.setCreatedAt(ts.toLocalDateTime());
        try { p.setInstructorName(rs.getString("instructor_name")); } catch (Exception ignored) {}
        try { p.setMemberName(rs.getString("member_name")); } catch (Exception ignored) {}
        return p;
    };

    private final RowMapper<WorkoutDay> dayMapper = (rs, rowNum) -> {
        WorkoutDay d = new WorkoutDay();
        d.setId(rs.getLong("id"));
        d.setProgramId(rs.getLong("program_id"));
        d.setDayName(rs.getString("day_name"));
        d.setFocus(rs.getString("focus"));
        d.setDayOrder(rs.getInt("day_order"));
        return d;
    };

    private final RowMapper<WorkoutExercise> exerciseMapper = (rs, rowNum) -> {
        WorkoutExercise e = new WorkoutExercise();
        e.setId(rs.getLong("id"));
        e.setDayId(rs.getLong("day_id"));
        e.setName(rs.getString("exercise_name"));
        int sets = rs.getInt("sets");
        e.setSets(rs.wasNull() ? null : sets);
        e.setReps(rs.getString("reps"));
        int rest = rs.getInt("rest_seconds");
        e.setRestSeconds(rs.wasNull() ? null : rest);
        e.setExerciseOrder(rs.getInt("exercise_order"));
        return e;
    };

    // ── Workout Program CRUD ───────────────────────────────────────

    public WorkoutProgram save(WorkoutProgram program) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO workout_programs (program_name, instructor_id, member_id, fitness_goal, difficulty, " +
                            "duration_weeks, sessions_per_week, session_duration_min, description, status) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, program.getProgramName());
            ps.setInt(2, program.getInstructorId());
            setNullableInt(ps, 3, program.getMemberId());
            ps.setString(4, program.getFitnessGoal());
            ps.setString(5, program.getDifficulty());
            setNullableInt(ps, 6, program.getDurationWeeks());
            setNullableInt(ps, 7, program.getSessionsPerWeek());
            setNullableInt(ps, 8, program.getSessionDurationMin());
            ps.setString(9, program.getDescription());
            ps.setString(10, program.getStatus() != null ? program.getStatus() : "ACTIVE");
            return ps;
        }, kh);
        program.setId(kh.getKey().longValue());

        // Save days and exercises
        if (program.getDays() != null) {
            int dayOrder = 0;
            for (WorkoutDay day : program.getDays()) {
                saveDay(program.getId(), day, dayOrder++);
            }
        }
        return program;
    }

    private void saveDay(Long programId, WorkoutDay day, int order) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO workout_days (program_id, day_name, focus, day_order) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, programId);
            ps.setString(2, day.getDayName());
            ps.setString(3, day.getFocus());
            ps.setInt(4, order);
            return ps;
        }, kh);
        day.setId(kh.getKey().longValue());

        if (day.getExercises() != null) {
            int exOrder = 0;
            for (WorkoutExercise ex : day.getExercises()) {
                saveExercise(day.getId(), ex, exOrder++);
            }
        }
    }

    private void saveExercise(Long dayId, WorkoutExercise ex, int order) {
        jdbcTemplate.update(
                "INSERT INTO workout_exercises (day_id, exercise_name, sets, reps, rest_seconds, exercise_order) VALUES (?, ?, ?, ?, ?, ?)",
                dayId, ex.getName(), ex.getSets(), ex.getReps(), ex.getRestSeconds(), order);
    }

    public void delete(Long id) {
        // Find day IDs to delete exercises
        List<Long> dayIds = jdbcTemplate.queryForList(
                "SELECT id FROM workout_days WHERE program_id = ?", Long.class, id);
        for (Long dayId : dayIds) {
            jdbcTemplate.update("DELETE FROM workout_exercises WHERE day_id = ?", dayId);
        }
        jdbcTemplate.update("DELETE FROM workout_days WHERE program_id = ?", id);
        jdbcTemplate.update("DELETE FROM workout_programs WHERE id = ?", id);
    }

    // ── Queries ────────────────────────────────────────────────────

    public List<WorkoutProgram> findByInstructorId(int instructorId) {
        String sql = "SELECT wp.*, u.name AS member_name FROM workout_programs wp " +
                "LEFT JOIN users u ON wp.member_id = u.id " +
                "WHERE wp.instructor_id = ? ORDER BY wp.created_at DESC";
        List<WorkoutProgram> programs = jdbcTemplate.query(sql, programMapper, instructorId);
        programs.forEach(this::loadDays);
        return programs;
    }

    public List<WorkoutProgram> findByMemberId(int memberId) {
        String sql = "SELECT wp.*, u.name AS instructor_name FROM workout_programs wp " +
                "LEFT JOIN users u ON wp.instructor_id = u.id " +
                "WHERE wp.member_id = ? ORDER BY wp.created_at DESC";
        List<WorkoutProgram> programs = jdbcTemplate.query(sql, programMapper, memberId);
        programs.forEach(this::loadDays);
        return programs;
    }

    public List<WorkoutProgram> findAll() {
        String sql = "SELECT wp.*, ins.name AS instructor_name, mem.name AS member_name " +
                "FROM workout_programs wp " +
                "LEFT JOIN users ins ON wp.instructor_id = ins.id " +
                "LEFT JOIN users mem ON wp.member_id = mem.id " +
                "ORDER BY wp.created_at DESC";
        List<WorkoutProgram> programs = jdbcTemplate.query(sql, programMapper);
        programs.forEach(this::loadDays);
        return programs;
    }

    public int countByInstructorId(int instructorId) {
        Integer c = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM workout_programs WHERE instructor_id = ?", Integer.class, instructorId);
        return c != null ? c : 0;
    }

    private void loadDays(WorkoutProgram program) {
        List<WorkoutDay> days = jdbcTemplate.query(
                "SELECT * FROM workout_days WHERE program_id = ? ORDER BY day_order",
                dayMapper, program.getId());
        for (WorkoutDay day : days) {
            List<WorkoutExercise> exercises = jdbcTemplate.query(
                    "SELECT * FROM workout_exercises WHERE day_id = ? ORDER BY exercise_order",
                    exerciseMapper, day.getId());
            day.setExercises(exercises);
        }
        program.setDays(days);
    }

    // ── Helpers ────────────────────────────────────────────────────

    private void setNullableInt(PreparedStatement ps, int idx, Integer value) throws java.sql.SQLException {
        if (value == null) ps.setNull(idx, java.sql.Types.INTEGER);
        else ps.setInt(idx, value);
    }
}
