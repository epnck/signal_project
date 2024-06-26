package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * The TcpOutputStrategy class represents an output strategy that sends patient data over tcp.
 * For each patient this data consists of patient ID, the timestamp when the data was taken,
 * the label of the data, and the actual health data.
 * This class implements the OutputStrategy interface.
 *
 * @author Tom Pepels
 */

public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Constructs a new TcpOutputStrategy that listens on the specified port.
     *
     * @param port The port on which the TCP server will listen for client connections.
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Outputs the provided data for a specified patient over the established TCP connection.
     *
     * @param patientId The ID of the patient.
     * @param timestamp The timestamp of the data.
     * @param label The label associated with the data.
     * @param data The data to be sent over the TCP connection.
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
