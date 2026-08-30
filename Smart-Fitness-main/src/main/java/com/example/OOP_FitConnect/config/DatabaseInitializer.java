package com.example.OOP_FitConnect.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Order(1)
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initSchema() {
        try {
            // users table
            jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            email VARCHAR(255) NOT NULL UNIQUE,
                            password VARCHAR(255) NOT NULL,
                            verificationCode INT DEFAULT 0,
                            branch VARCHAR(100)
                        )
                    """);

            // Add columns that may not exist yet (ALTER ignores if already present via try/catch)
            tryAlter("ALTER TABLE users ADD COLUMN current_plan_id INT DEFAULT NULL");
            tryAlter("ALTER TABLE users ADD COLUMN role VARCHAR(20) DEFAULT 'USER'");

            // Membership plans
            jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS membership_plans (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            price DOUBLE NOT NULL,
                            duration_months INT NOT NULL DEFAULT 1,
                            features TEXT,
                            popular BOOLEAN DEFAULT FALSE
                        )
                    """);

            // Payments
            jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS payments (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            user_id INT NOT NULL,
                            plan_id INT,
                            amount DOUBLE NOT NULL,
                            payment_method VARCHAR(50),
                            status VARCHAR(20) DEFAULT 'completed',
                            payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                            KEY idx_user_id (user_id),
                            KEY idx_plan_id (plan_id)
                        )
                    """);

            // Diet Plans
            jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS diet_plans (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            plan_name VARCHAR(255) NOT NULL,
                            instructor_id INT NOT NULL,
                            member_id INT DEFAULT NULL,
                            goal VARCHAR(50),
                            daily_calories INT,
                            duration_weeks INT,
                            protein_grams INT,
                            carbs_grams INT,
                            fat_grams INT,
                            notes TEXT,
                            status VARCHAR(20) DEFAULT 'ACTIVE',
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                        )
                    """);

            // Diet Plan Meals
            jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS diet_plan_meals (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            diet_plan_id BIGINT NOT NULL,
                            meal_type VARCHAR(50),
                            food_items TEXT,
                            calories INT
                        )
                    """);

            // Workout Programs
            jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS workout_programs (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            program_name VARCHAR(255) NOT NULL,
                            instructor_id INT NOT NULL,
                            member_id INT DEFAULT NULL,
                            fitness_goal VARCHAR(50),
                            difficulty VARCHAR(20),
                            duration_weeks INT,
                            sessions_per_week INT,
                            session_duration_min INT,
                            description TEXT,
                            status VARCHAR(20) DEFAULT 'ACTIVE',
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                        )
                    """);

            // Workout Days
            jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS workout_days (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            program_id BIGINT NOT NULL,
                            day_name VARCHAR(20),
                            focus VARCHAR(50),
                            day_order INT DEFAULT 0
                        )
                    """);

            // Workout Exercises
            jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS workout_exercises (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            day_id BIGINT NOT NULL,
                            exercise_name VARCHAR(255),
                            sets INT,
                            reps VARCHAR(50),
                            rest_seconds INT,
                            exercise_order INT DEFAULT 0
                        )
                    """);

            // Announcements
            jdbcTemplate.execute("""
                        CREATE TABLE IF NOT EXISTS announcements (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            content TEXT NOT NULL,
                            posted_by VARCHAR(100),
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            active BOOLEAN DEFAULT TRUE
                        )
                    """);

            System.out.println("[DatabaseInitializer] Schema initialized successfully.");
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] WARNING: Schema init failed: " + e.getMessage());
        }
    }

    private void tryAlter(String sql) {
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ignored) {
            // Column already exists — safe to ignore
        }
    }

    @Override
    public void run(String... args) {
        try {
            Integer planCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM membership_plans", Integer.class);
            if (planCount != null && planCount == 0) {
                jdbcTemplate.update(
                        "INSERT INTO membership_plans (name, description, price, duration_months, features, popular) VALUES (?, ?, ?, ?, ?, ?)",
                        "Basic Plan", "Perfect for beginners", 10000.00, 1,
                        "Access to gym equipment|Locker room access", false);
                jdbcTemplate.update(
                        "INSERT INTO membership_plans (name, description, price, duration_months, features, popular) VALUES (?, ?, ?, ?, ?, ?)",
                        "Student Plan", "Affordable for students", 7000.00, 1,
                        "Access to gym equipment|Locker room access|Group classes (limited)", false);
                jdbcTemplate.update(
                        "INSERT INTO membership_plans (name, description, price, duration_months, features, popular) VALUES (?, ?, ?, ?, ?, ?)",
                        "Couples Plan", "Workout together & save", 15000.00, 1,
                        "Access to gym equipment|Locker room access|Group Classes|3 Personal Training Sessions", false);
                jdbcTemplate.update(
                        "INSERT INTO membership_plans (name, description, price, duration_months, features, popular) VALUES (?, ?, ?, ?, ?, ?)",
                        "Family Plan", "Perfect for the whole family", 20000.00, 1,
                        "Access for 4 family members|Group Classes for all|4 Personal Training Sessions|Family Discount on Supplements",
                        false);
                jdbcTemplate.update(
                        "INSERT INTO membership_plans (name, description, price, duration_months, features, popular) VALUES (?, ?, ?, ?, ?, ?)",
                        "Standard Plan", "Great for regulars", 25000.00, 1,
                        "Access to gym equipment|Locker room access|2 Personal Training Sessions", false);
                jdbcTemplate.update(
                        "INSERT INTO membership_plans (name, description, price, duration_months, features, popular) VALUES (?, ?, ?, ?, ?, ?)",
                        "Premium Plan", "Best for serious athletes", 35000.00, 1,
                        "Access to gym equipment|Locker room access|Unlimited Personal Training|Unlimited Group Classes",
                        true);
                System.out.println("[DatabaseInitializer] Default plans seeded.");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] WARNING: Seeding failed: " + e.getMessage());
        }

        System.out.println("Database tables initialized successfully.");
    }
}
