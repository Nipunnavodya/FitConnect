package repository;

import com.example.OOP_FitConnect.model.Announcement;
import com.example.OOP_FitConnect.model.MembershipPlan;
import com.example.OOP_FitConnect.model.Payment;
import com.example.OOP_FitConnect.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DBController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ======================== USER ========================

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setVerificationCode(rs.getInt("verificationCode"));
            user.setBranch(rs.getString("branch"));
            int planId = rs.getInt("current_plan_id");
            user.setCurrentPlanId(rs.wasNull() ? null : planId);
            // Load persisted role; getRole() still overrides for the admin email
            try {
                String dbRole = rs.getString("role");
                if (dbRole != null) user.setRole(dbRole);
            } catch (SQLException ignored) {
                // Column may not exist yet on first startup — safe to skip
            }
            return user;
        }
    };

    public User saveUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (name, email, password, verificationCode, branch, role) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getVerificationCode());
            ps.setString(5, user.getBranch());
            ps.setString(6, user.getRole() != null ? user.getRole() : "USER");
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    public User updateUser(User user) {
        jdbcTemplate.update(
                "UPDATE users SET name = ?, email = ?, password = ?, verificationCode = ?, branch = ?, current_plan_id = ?, role = ? WHERE id = ?",
                user.getName(), user.getEmail(), user.getPassword(),
                user.getVerificationCode(), user.getBranch(), user.getCurrentPlanId(),
                user.getRole() != null ? user.getRole() : "USER",
                user.getId()
        );
        return user;
    }

    public void updateUserPlan(int userId, Integer planId) {
        jdbcTemplate.update("UPDATE users SET current_plan_id = ? WHERE id = ?", planId, userId);
    }

    public User getUserById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?", userRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User getUserByEmail(String email) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ?", userRowMapper, email);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public User getUserByVerificationCode(int code) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE verificationCode = ? AND verificationCode != 0",
                    userRowMapper, code
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void deleteUser(int id) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", userRowMapper);
    }

    /** Returns only members (role = USER). */
    public List<User> getAllMembers() {
        return jdbcTemplate.query("SELECT * FROM users WHERE role = 'USER'", userRowMapper);
    }

    // ======================== MEMBERSHIP PLANS ========================

    private final RowMapper<MembershipPlan> planRowMapper = (rs, rowNum) -> {
        MembershipPlan plan = new MembershipPlan();
        plan.setId(rs.getInt("id"));
        plan.setName(rs.getString("name"));
        plan.setDescription(rs.getString("description"));
        plan.setPrice(rs.getDouble("price"));
        plan.setDurationMonths(rs.getInt("duration_months"));
        plan.setFeatures(rs.getString("features"));
        plan.setPopular(rs.getBoolean("popular"));
        return plan;
    };

    public List<MembershipPlan> getAllPlans() {
        return jdbcTemplate.query("SELECT * FROM membership_plans", planRowMapper);
    }

    public MembershipPlan getPlanById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM membership_plans WHERE id = ?", planRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public MembershipPlan savePlan(MembershipPlan plan) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO membership_plans (name, description, price, duration_months, features, popular) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, plan.getName());
            ps.setString(2, plan.getDescription());
            ps.setDouble(3, plan.getPrice());
            ps.setInt(4, plan.getDurationMonths());
            ps.setString(5, plan.getFeatures());
            ps.setBoolean(6, plan.isPopular());
            return ps;
        }, keyHolder);
        plan.setId(keyHolder.getKey().intValue());
        return plan;
    }

    public MembershipPlan updatePlan(MembershipPlan plan) {
        jdbcTemplate.update(
                "UPDATE membership_plans SET name = ?, description = ?, price = ?, duration_months = ?, features = ?, popular = ? WHERE id = ?",
                plan.getName(), plan.getDescription(), plan.getPrice(),
                plan.getDurationMonths(), plan.getFeatures(), plan.isPopular(), plan.getId()
        );
        return plan;
    }

    public void deletePlan(int id) {
        jdbcTemplate.update("DELETE FROM membership_plans WHERE id = ?", id);
    }

    // ======================== PAYMENTS ========================

    private final RowMapper<Payment> paymentRowMapper = (rs, rowNum) -> {
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setUserId(rs.getInt("user_id"));
        int planId = rs.getInt("plan_id");
        payment.setPlanId(rs.wasNull() ? 0 : planId);
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("payment_date");
        payment.setPaymentDate(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return payment;
    };

    public List<Payment> getAllPayments() {
        return jdbcTemplate.query(
                "SELECT p.*, u.name AS user_name, mp.name AS plan_name FROM payments p " +
                "LEFT JOIN users u ON p.user_id = u.id " +
                "LEFT JOIN membership_plans mp ON p.plan_id = mp.id " +
                "ORDER BY p.payment_date DESC",
                (rs, rowNum) -> {
                    Payment payment = paymentRowMapper.mapRow(rs, rowNum);
                    payment.setUserName(rs.getString("user_name"));
                    payment.setPlanName(rs.getString("plan_name"));
                    return payment;
                }
        );
    }

    public List<Payment> getPaymentsByUserId(int userId) {
        return jdbcTemplate.query(
                "SELECT p.*, u.name AS user_name, mp.name AS plan_name FROM payments p " +
                "LEFT JOIN users u ON p.user_id = u.id " +
                "LEFT JOIN membership_plans mp ON p.plan_id = mp.id " +
                "WHERE p.user_id = ? ORDER BY p.payment_date DESC",
                (rs, rowNum) -> {
                    Payment payment = paymentRowMapper.mapRow(rs, rowNum);
                    payment.setUserName(rs.getString("user_name"));
                    payment.setPlanName(rs.getString("plan_name"));
                    return payment;
                },
                userId
        );
    }

    public Payment savePayment(Payment payment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO payments (user_id, plan_id, amount, payment_method, status, payment_date) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, payment.getUserId());
            ps.setInt(2, payment.getPlanId());
            ps.setDouble(3, payment.getAmount());
            ps.setString(4, payment.getPaymentMethod());
            ps.setString(5, payment.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(payment.getPaymentDate()));
            return ps;
        }, keyHolder);
        payment.setId(keyHolder.getKey().intValue());
        return payment;
    }

    public double getTotalRevenue() {
        Double total = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE status = 'completed'", Double.class
        );
        return total != null ? total : 0.0;
    }

    // ======================== INSTRUCTORS ========================

    /** Returns only instructors (role = INSTRUCTOR). */
    public List<User> getAllInstructors() {
        return jdbcTemplate.query("SELECT * FROM users WHERE role = 'INSTRUCTOR'", userRowMapper);
    }

    // ======================== ANNOUNCEMENTS ========================

    private final RowMapper<Announcement> announcementRowMapper = (rs, rowNum) -> {
        Announcement a = new Announcement();
        a.setId(rs.getLong("id"));
        a.setTitle(rs.getString("title"));
        a.setContent(rs.getString("content"));
        a.setPostedBy(rs.getString("posted_by"));
        Timestamp ts = rs.getTimestamp("created_at");
        a.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        a.setActive(rs.getBoolean("active"));
        return a;
    };

    public List<Announcement> getAllAnnouncements() {
        return jdbcTemplate.query(
                "SELECT * FROM announcements ORDER BY created_at DESC", announcementRowMapper);
    }

    public List<Announcement> getActiveAnnouncements() {
        return jdbcTemplate.query(
                "SELECT * FROM announcements WHERE active = TRUE ORDER BY created_at DESC", announcementRowMapper);
    }

    public Announcement saveAnnouncement(Announcement a) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO announcements (title, content, posted_by) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getContent());
            ps.setString(3, a.getPostedBy());
            return ps;
        }, keyHolder);
        a.setId(keyHolder.getKey().longValue());
        return a;
    }

    public void deleteAnnouncement(Long id) {
        jdbcTemplate.update("DELETE FROM announcements WHERE id = ?", id);
    }

    public void toggleAnnouncement(Long id, boolean active) {
        jdbcTemplate.update("UPDATE announcements SET active = ? WHERE id = ?", active, id);
    }
}
