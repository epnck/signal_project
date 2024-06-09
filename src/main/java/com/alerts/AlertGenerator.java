package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.monitors.*;

import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private long startTime = 0;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        long endTime = System.currentTimeMillis();
        List<PatientRecord> patientRecords = patient.getRecords(startTime, endTime);
        String patientID = Integer.toString(patient.getPatientId());


        BloodPressureMonitor bloodPressureMonitor = new BloodPressureMonitor();
        HypotensiveHypoxemiaMonitor hypotensiveHypoxemiaMonitor = new HypotensiveHypoxemiaMonitor();


        for (PatientRecord record : patientRecords){
            long timeStamp = record.getTimestamp();

            //data generated with the blood pressure generator is categorized under diastolic and systolic, not under the general label "blood pressure"
            //blood pressure validation
            if(record.getRecordType().equals("DiastolicPressure")){
               bloodPressureMonitor.validateData(record);

            } else if (record.getRecordType().equals("SystolicPressure")) {
                bloodPressureMonitor.validateData(record);
                hypotensiveHypoxemiaMonitor.validateData(record);
            }

            switch (bloodPressureMonitor.getState()) {
                case TREND_ALERT:
                    triggerAlert(new Alert(patientID, "Blood Pressure Trend Alert", timeStamp));
                    bloodPressureMonitor.resetState();
                    break;
                case CRITICAL_ALERT:
                    triggerAlert(new Alert(patientID, "Blood Pressure Critical Threshold Surpassed", timeStamp));
                    bloodPressureMonitor.resetState();
                    break;
            }




        }
    }



    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        // Implementation might involve logging the alert or notifying staff
    }

}
