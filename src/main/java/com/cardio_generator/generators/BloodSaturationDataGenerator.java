package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * The class BloodSaturationDataGenerator represents a patient data generator for
 * blood saturation levels.
 * This class is responsible for generating blood saturation data for patients, to be used
 * in the HealthDataSimulator class as health data under the label 'Saturation'.
 * It utilizes a pseudo-random number generator to simulate real-time patient data variations.
 *
 * @author Tom Pepels
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();
    private int[] lastSaturationValues;

    /**
     * Constructs a BloodSaturationDataGenerator and creates an array containing baseline saturation
     * values for each patient.
     * The initial values will be between 95 and 100.
     *
     * @param patientCount The number of patients.
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates blood saturation data for a given patient and outputs it using the specified output strategy.
     * The simulated blood saturation values will range between 90% and 100%.
     *
     * @param patientId The patient ID of the patient to generate data for.
     * @param outputStrategy The strategy used to output the data. Either console, file, WebSocket, or TCP socket.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
