package com.data_management;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;


public class FileDataReader implements DataReader{
    private String filePath;

    public FileDataReader(String filePath) {
        this.filePath = filePath;
    }

    public void readData(DataStorage dataStorage) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                //If a line starts with Patient ID:, the line contains the relevant data and should be processed
                if (line.startsWith("Patient ID:")) {
                   proccessLine(line, dataStorage);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file from directory: " + e.getMessage());
            throw e;
        }
    }

    //Processes the specified into the following fields: patientID, timestamp, label and patient data
    private void proccessLine(String line, DataStorage dataStorage){
        String[] fileSplits = line.split(", ");
        int patientId = Integer.parseInt(fileSplits[0].split(": ")[1]);
        long timestamp = Long.parseLong(fileSplits[1].split(": ")[1]);
        String label = fileSplits[2].split(": ")[1];
        double patientData = Double.parseDouble(fileSplits[3].split(": ")[1]);

        //Adds the data to the data storage for the corresponding patient
        dataStorage.addPatientData(patientId, patientData, label, timestamp);
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getFilePath(){
        return filePath;
    }
}



