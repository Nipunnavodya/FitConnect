package repository;

import com.example.OOP_FitConnect.model.Equipment;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EquipmentRepository {
    private static final String CSV_FILE = "equipments.csv";
    private final Map<String, Equipment> equipmentById = new ConcurrentHashMap<>();

    public EquipmentRepository() {
        loadFromCSV();
    }

    @PostConstruct
    private void loadFromCSV() {
        File file = new File(CSV_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                Equipment eq = equipmentFromCSV(line);
                if (eq != null) equipmentById.put(eq.getId(), eq);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void saveToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println("id,name,description,quantity,status,lastServiced");
            for (Equipment eq : equipmentById.values()) {
                pw.println(equipmentToCSV(eq));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public synchronized List<Equipment> getAll() {
        return new ArrayList<>(equipmentById.values());
    }

    public synchronized void add(Equipment eq) {
        equipmentById.put(eq.getId(), eq);
        saveToCSV();
    }

    public synchronized void delete(String id) {
        equipmentById.remove(id);
        saveToCSV();
    }

    private String equipmentToCSV(Equipment eq) {
        return String.join(",",
            safe(eq.getId()),
            safe(eq.getName()),
            safe(eq.getDescription()),
            String.valueOf(eq.getQuantity()),
            safe(eq.getStatus()),
            safe(eq.getLastServiced())
        );
    }

    private Equipment equipmentFromCSV(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) return null;
        Equipment eq = new Equipment();
        eq.setId(parts[0]);
        eq.setName(parts[1]);
        eq.setDescription(parts[2]);
        eq.setQuantity(Integer.parseInt(parts[3]));
        eq.setStatus(parts[4]);
        eq.setLastServiced(parts[5]);
        return eq;
    }

    private String safe(String s) {
        return s == null ? "" : s.replace(",", "");
    }

    public synchronized Equipment getById(String id) {
        return equipmentById.get(id);
    }
}
