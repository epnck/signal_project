package com.cardio_generator.outputs;

/**
 * This interface represents a strategy for outputting health data. Implementations
 * of this interface define how generated data is to be outputted.
 * <p>
 * Data can be outputted in either console, file, WebSocket, or TCP socket.
 * </p>
 *
 * @author Tom Pepels
 */
public interface OutputStrategy {
    /**
     * Outputs the data for a specified patient.
     *
     * @param patientId The ID of the patient to output data for.
     * @param timestamp The timestamp associated with the data.
     * @param label The label corresponding to the data type.
     * @param data The data to be outputted.
     */
    void output(int patientId, long timestamp, String label, String data);
}
