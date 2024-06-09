package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.monitors.*;

import java.util.ArrayList;
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
    private List<Alert> alertLog = new ArrayList<>();
    public boolean bloodPressureTriggerd = false;
    public boolean bloodSaturationTriggered = false;
    public boolean hypoTriggered = false; //hypotensive hypoxemia
    public boolean ecgTriggerd = false;

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

        //for testing
        bloodPressureTriggerd = false;
        bloodSaturationTriggered = false;
        hypoTriggered = false;
        ecgTriggerd = false;

        BloodPressureMonitor bloodPressureMonitor = new BloodPressureMonitor();
        BloodSaturationMonitor bloodSaturationMonitor = new BloodSaturationMonitor();
        HypotensiveHypoxemiaMonitor hypotensiveHypoxemiaMonitor = new HypotensiveHypoxemiaMonitor();
        EcgMonitor ecgMonitor = new EcgMonitor();


        for (PatientRecord record : patientRecords){
            long timeStamp = record.getTimestamp();

            //add record to the correct monitor
            switch (record.getRecordType()) {
                //data generated with the blood pressure generator is categorized under diastolic and systolic, not under the general label "blood pressure"
                case "DiastolicPressure":
                    bloodPressureMonitor.validateData(record);
                    break;
                case "SystolicPressure":
                    bloodPressureMonitor.validateData(record);
                    hypotensiveHypoxemiaMonitor.validateData(record);
                    break;
                case "BloodSaturation":
                    bloodSaturationMonitor.validateData(record);
                    hypotensiveHypoxemiaMonitor.validateData(record);
                    break;
                case "ECG":
                    ecgMonitor.validateData(record);
                    break;
            }

            //check for hypotensive hypoxemia alert
            if(hypotensiveHypoxemiaMonitor.getState() == HypotensiveHypoxemiaMonitor.State.HH_ALERT){
                triggerAlert(new Alert(patientID, "Hypotensive Hypoxemia Alert", timeStamp));
                hypoTriggered = true;

                //reset all relevant states to prevent sending already dealt with errors
                bloodPressureMonitor.resetState();
                bloodSaturationMonitor.resetState();
                hypotensiveHypoxemiaMonitor.resetState();;
                break; //no need to go over other alerts, since each added record can only trigger one alert
            }

            //check for ecg alers
            if(ecgMonitor.getState() == EcgMonitor.State.ECG_ALERT){
                triggerAlert(new Alert(patientID, "ECG Peak Alert", timeStamp));
                ecgTriggerd = true;
                ecgMonitor.resetState();
                break; //no need to go over other alerts, since each added record can only trigger one alert
            }


            //check for blood pressure alerts
            switch (bloodPressureMonitor.getState()) {
                case TREND_ALERT:
                    triggerAlert(new Alert(patientID, "Blood Pressure Trend Alert", timeStamp));
                    bloodPressureMonitor.resetState();
                    bloodPressureTriggerd = true;
                    break;
                case CRITICAL_ALERT:
                    triggerAlert(new Alert(patientID, "Blood Pressure Critical Threshold Surpassed", timeStamp));
                    bloodPressureTriggerd = true;
                    bloodPressureMonitor.resetState();
                    break;
            }

            //check for blood saturation alerts
            switch (bloodSaturationMonitor.getState()){
                case LOW_SATURATION_ALERT:
                    triggerAlert(new Alert(patientID, "Low Blood Saturation Alert", timeStamp));
                    bloodSaturationTriggered = true;
                    bloodSaturationMonitor.resetState();
                    break;
                case RAPID_DROP_ALERT:
                    triggerAlert(new Alert(patientID, "Rapid Drop in Blood Saturation Alert", timeStamp));
                    bloodSaturationTriggered = true;
                    bloodSaturationMonitor.resetState();
                    break;
            }
        }

        //everything before the current end time has been check
        //update start time for next method call
        //commented out for testing
        //startTime = endTime;
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
        alertLog.add(alert);
        System.out.println("Alert for Patient "+alert.getPatientId()+": "+alert.getCondition()+" at: "+alert.getTimestamp()+"!!ALERT!!");
    }

}
