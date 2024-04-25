package com.cardio_generator.generators;

// Removed blank line, all import statements should be in one block
import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * The class AlertGenerator implements the PatientDataGenerator interface and is
 * responsible for generating medical alerts for patients. The alerts can either be resolved or
 * triggered based on a randomly generated value.
 *
 * @author Tom Pepels
 */
public class AlertGenerator implements PatientDataGenerator {
    // Changed constant name to UPPER_SNAKE_CASE
    public static final Random RANDOM_GENERATOR = new Random();
    // Changed variable name to camelCase
    private boolean[] alertStates; // false = resolved, true = pressed

    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates alerts for patients. For each alert a random number between 0 and 1 is generated.
     * If the value of the number is lower than 0.9 the alert will be marked as resolved, otherwise
     * the alert will be marked as triggered.
     *
     * @param patientId The patient ID of the patient to generate data for.
     * @param outputStrategy The strategy used to output the data. Either console, file, WebSocket, or TCP socket.
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name to camelCase
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
