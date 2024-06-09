package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class WebSocketDataReader implements DataReader {
    private final int port;

    public WebSocketDataReader(int port){
        this.port = port;
    }

    @Override
    public void startReading(DataStorage dataStorage) throws IOException {
        try {
            URI uri = new URI("ws://localhost:" + port);
            WebSocketClient client = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to server");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Received: " + message);
                    try {
                        //If a line starts with Patient ID:, the line contains the relevant data and should be processed
                        if (message.startsWith("Patient ID:")) {
                            proccessMessage(message, dataStorage);
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing message: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected from server: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("WebSocket error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            };
            client.connectBlocking(); //synchronous connect
            System.out.println("WebSocket Client connected");
        } catch (URISyntaxException | InterruptedException e) {
            throw new IOException("Error connecting to WebSocket server", e);
        }
    }

    private void proccessMessage(String message, DataStorage dataStorage){
        try {
            String[] fileSplits = message.split(", ");
            int patientId = Integer.parseInt(fileSplits[0].split(": ")[1]);
            long timestamp = Long.parseLong(fileSplits[1].split(": ")[1]);
            String label = fileSplits[2].split(": ")[1];
            double patientData = Double.parseDouble(fileSplits[3].split(": ")[1]);

            //Adds the data to the data storage for the corresponding patient
            dataStorage.addPatientData(patientId, patientData, label, timestamp);
        } catch (Exception e) { //error when parsing
            System.err.println("Error parsing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}