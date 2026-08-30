package service;

import org.springframework.stereotype.Service;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class ContactService {

    private static final String CSV_FILE_PATH = "contact_submissions.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveContactForm(Map<String, String> formData) throws IOException {
        // Create the CSV file if it doesn't exist
        Path filePath = Paths.get(CSV_FILE_PATH);
        boolean fileExists = Files.exists(filePath);

        try (FileWriter writer = new FileWriter(CSV_FILE_PATH, true)) {
            // Write header if file is new
            if (!fileExists) {
                writer.append("Timestamp,FirstName,LastName,Street,City,Email,Message\n");
            }

            // Write the form data
            writer.append(LocalDateTime.now().format(DATE_FORMATTER)).append(",")
                  .append(escapeCsv(formData.get("firstName"))).append(",")
                  .append(escapeCsv(formData.get("lastName"))).append(",")
                  .append(escapeCsv(formData.get("street"))).append(",")
                  .append(escapeCsv(formData.get("city"))).append(",")
                  .append(escapeCsv(formData.get("email"))).append(",")
                  .append(escapeCsv(formData.get("message")))
                  .append("\n");
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        // Escape quotes and wrap in quotes if contains comma or quote
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
} 
