package com.example.OOP_FitConnect.controller;

import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/admin/api/classes")
public class AdminClassController {
    private static final String CSV_FILE = "classes.csv";
    private static final String BOOKINGS_FILE = "bookings.csv";

    @GetMapping
    public List<Map<String, String>> getAllClasses() throws IOException {
        List<Map<String, String>> classes = new ArrayList<>();
        File file = new File(CSV_FILE);
        if (!file.exists()) return classes;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header == null) return classes;
            String[] columns = header.split(",");
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < columns.length && i < values.length; i++) {
                    map.put(columns[i], values[i]);
                }
                classes.add(map);
            }
        }
        return classes;
    }

    @PostMapping
    public void addClass(@RequestBody Map<String, String> classData) throws IOException {
        File file = new File(CSV_FILE);
        boolean exists = file.exists();
        try (FileWriter fw = new FileWriter(file, true)) {
            if (!exists) {
                fw.write("id,name,date,time,duration,instructor\n");
            }
            String id = UUID.randomUUID().toString();
            fw.write(id + "," +
                    classData.getOrDefault("name","") + "," +
                    classData.getOrDefault("date","") + "," +
                    classData.getOrDefault("time","") + "," +
                    classData.getOrDefault("duration","") + "," +
                    classData.getOrDefault("instructor","") + "\n");
        }
    }

    @PutMapping("/{id}")
    public void editClass(@PathVariable String id, @RequestBody Map<String, String> classData) throws IOException {
        File file = new File(CSV_FILE);
        if (!file.exists()) return;
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header != null) lines.add(header);
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && values[0].equals(id)) {
                    lines.add(id + "," +
                            classData.getOrDefault("name","") + "," +
                            classData.getOrDefault("date","") + "," +
                            classData.getOrDefault("time","") + "," +
                            classData.getOrDefault("duration","") + "," +
                            classData.getOrDefault("instructor","")
                    );
                } else {
                    lines.add(line);
                }
            }
        }
        try (FileWriter fw = new FileWriter(file, false)) {
            for (String l : lines) fw.write(l + "\n");
        }
    }

    @DeleteMapping("/{id}")
    public void deleteClass(@PathVariable String id) throws IOException {
        File file = new File(CSV_FILE);
        if (!file.exists()) return;
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header != null) lines.add(header);
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 0 && !values[0].equals(id)) {
                    lines.add(line);
                }
            }
        }
        try (FileWriter fw = new FileWriter(file, false)) {
            for (String l : lines) fw.write(l + "\n");
        }
    }

    @PostMapping("/{classId}/enroll")
    public void enrollUser(@PathVariable String classId, @RequestParam String userId) throws IOException {
        try (FileWriter fw = new FileWriter(BOOKINGS_FILE, true)) {
            fw.write(classId + "," + userId + "\n");
        }
    }

    @GetMapping("/{classId}/bookings")
    public int getBookingCount(@PathVariable String classId) throws IOException {
        File file = new File(BOOKINGS_FILE);
        if (!file.exists()) return 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            return (int) br.lines().filter(line -> line.startsWith(classId + ",")).count();
        }
    }
}
