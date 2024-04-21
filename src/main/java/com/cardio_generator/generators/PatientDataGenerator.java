package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * This interface represents a patient data generator, which is responsible for generating
 * health data for a specific patient. Implementations of this interface are expected
 * to generate patient data and output it using a provided OutputStrategy.
 *
 * @author Tom Pepels
 */
public interface PatientDataGenerator {
    /**
     * Generates health data for the specified patient and outputs it using the provided
     * OutputStrategy.
     *
     * @param patientId The patient ID of the patient to generate data for.
     * @param outputStrategy The strategy used to output the data. Either console, file, WebSocket, or TCP socket.
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
