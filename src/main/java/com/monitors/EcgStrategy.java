package com.monitors;

import com.alerts.Alert;
import com.alerts.alert_factories.EcgAlertFactory;
import com.data_management.PatientRecord;

import java.util.PriorityQueue;
import java.util.Queue;

public class EcgStrategy implements AlertStrategy {
    public enum State{
        NORMAL,
        ECG_ALERT,
    }
    private State state = State.NORMAL;
    private Queue<Double> readings = new PriorityQueue<>();
    private double sum = 0;
    private Alert alert;

    public EcgStrategy(){
    }


    @Override
    public boolean checkAlert(PatientRecord patientRecord) {
        //reset alert for each time method is called
        alert = null;

        //assuming readings are sent every minute, so one hour worht of data;
        int windowSize = 60;
        if (!readings.isEmpty() && readings.size() == windowSize) {
                sum -= readings.poll(); //remove the earliest data from the window
            }

            //add the data to the queue and the current sum
            double ecgData = patientRecord.getMeasurementValue();
            readings.add(ecgData);
            sum += ecgData;

            double average = sum / readings.size();

            if (ecgData > average * 1.15) { //assuming peak is defined as 15% higher than average
               state = State.ECG_ALERT;
                alert = new EcgAlertFactory().createAlert(Integer.toString(patientRecord.getPatientId()),
                        "High Peak", patientRecord.getTimestamp());
                resetState();
                return true;
            }

            return false;
    }


    public State getState(){
        return state;
    }

    public void resetState(){
        state = State.NORMAL;
    }
    public Alert getAlert(){
        return alert;
    }
}
