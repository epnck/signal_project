package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The FileOutputStrategy class represents an output strategy that writes data into files.
 * It creates a file that contains all relevant data of patients.
 * For each patient this consists of patient ID, the timestamp when the data was taken,
 * the label of the data, and the actual health data.
 * <p>
 * This class implements the OutputStrategy interface
 *
 * @author Tom Pepels
 */
// Changed class name to UpperCamelCase
public class FileOutputStrategy implements OutputStrategy {

    // Changed variable name to camelCase
    private String baseDirectory;
    // Changed variable name to camelCase
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a new FileOutputStrategy with the specified base directory.
     *
     * @param baseDirectory The base directory where files will be stored.
     */
    public FileOutputStrategy(String baseDirectory) {
        // Removed blank line for readability
        this.baseDirectory = baseDirectory;
    }

    /**
     * Outputs the provided data for a specified patient into a file containing all patient data.
     *
     * @param patientId The ID of the patient to output data for.
     * @param timestamp The timestamp associated with the data.
     * @param label The label corresponding to the data type.
     * @param data The data to be outputted.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the filePath variable
        // Changed variable name to camelCase and line-wrapped to conform to character limit
        String filePath = fileMap.computeIfAbsent(label,
                k -> Paths.get(baseDirectory, label + ".txt")
                        .toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                // Line-wrapped to conform to character limit
                Files.newBufferedWriter(
                        Paths.get(filePath),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND))) {
            // Line-wrapped to conform to character limit
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
                    patientId,
                    timestamp,
                    label,
                    data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}