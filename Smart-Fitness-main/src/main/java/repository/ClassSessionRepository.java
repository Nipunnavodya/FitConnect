package com.example.OOP_FitConnect.repository;

import com.example.OOP_FitConnect.model.ClassSession;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ClassSessionRepository {
    private static final String CSV_FILE = "classes.csv";
    private final Map<String, ClassSession> classesById = new ConcurrentHashMap<>();

    public ClassSessionRepository() {
        loadFromCSV();
    }

    @PostConstruct
    private void loadFromCSV() {
        File file = new File(CSV_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header
            br.readLine();
            while ((line = br.readLine()) != null) {
                ClassSession cs = classFromCSV(line);
                if (cs != null) {
                    classesById.put(cs.getId(), cs);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println("id,name,instructor,schedule");
            for (ClassSession cs : classesById.values()) {
                pw.println(classToCSV(cs));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<ClassSession> getAllClasses() {
        return new ArrayList<>(classesById.values());
    }

    public synchronized ClassSession getClassById(String id) {
        return classesById.get(id);
    }

    public synchronized void addClass(ClassSession cs) {
        classesById.put(cs.getId(), cs);
        saveToCSV();
    }

    public synchronized void updateClass(ClassSession cs) {
        if (classesById.containsKey(cs.getId())) {
            classesById.put(cs.getId(), cs);
            saveToCSV();
        }
    }

    public synchronized void deleteClass(String id) {
        classesById.remove(id);
        saveToCSV();
    }

    // CSV Serialization/Deserialization
    private String classToCSV(ClassSession cs) {
        return String.join(",",
                safe(cs.getId()),
                safe(cs.getName()),
                safe(cs.getInstructor()),
                safe(cs.getSchedule())
        );
    }

    private ClassSession classFromCSV(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 4) return null;

        ClassSession cs = new ClassSession();
        cs.setId(parts[0]);
        cs.setName(parts[1]);
        cs.setInstructor(parts[2]);
        cs.setSchedule(parts[3]);
        return cs;
    }

    private String safe(String s) {
        return s == null ? "" : s.replace(",", ""); // simple escaping, improve if needed
    }
}
