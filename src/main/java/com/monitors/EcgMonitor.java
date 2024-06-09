package com.monitors;

import com.data_management.PatientRecord;

import java.util.PriorityQueue;
import java.util.Queue;

public class EcgMonitor implements HealthDataMonitor {
    public enum State{
        NORMAL,
        ECG_ALERT,
    }
    private State state = State.NORMAL;
    private Queue<Double> readings = new PriorityQueue<>();
    private double sum = 0;

    public EcgMonitor(){
    }


    @Override
    public void validateData(PatientRecord patientRecord) {
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
            }
    }


    public State getState(){
        return state;
    }

    public void resetState(){
        state = State.NORMAL;
    }
}
