package com.monitors;

import com.alerts.Alert;
import com.alerts.alert_factories.BloodPressureAlertFactory;
import com.data_management.PatientRecord;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class BloodPressureStrategy implements AlertStrategy {
    public enum State{
        NORMAL,
      TREND_ALERT,
       CRITICAL_ALERT,

    }
    private State state = State.NORMAL;
    List<Double> diastolicReadings = new ArrayList<>();
    List<Double> systolicReadings = new ArrayList<>();
    private Alert alert;


    public BloodPressureStrategy(){

    }


    @Override
    public boolean checkAlert(PatientRecord patientRecord) {
        //reset alert for each time method is called
        alert = null;

        if(patientRecord.getRecordType().equals("DiastolicPressure")){
            monitorDiastolicPressure(patientRecord);
        }else{
            monitorSystolicPressure(patientRecord);
        }

        //check if any alerts are triggered and create them
        if(state == State.TREND_ALERT){
            alert = new BloodPressureAlertFactory().createAlert(Integer.toString(patientRecord.getPatientId()),
                    "Trend Alert", patientRecord.getTimestamp());
            resetState();
            return true;
        } else if (state == State.CRITICAL_ALERT) {
            alert = new BloodPressureAlertFactory().createAlert(Integer.toString(patientRecord.getPatientId()),
                    "Critical Threshold Surpassed", patientRecord.getTimestamp());
            resetState();
            return true;
        }

        //no alerts triggered so return false
        return false;
    }


    private void monitorDiastolicPressure(PatientRecord record) {
        double bloodPressureReading = record.getMeasurementValue();
        diastolicReadings.add(bloodPressureReading);

        //diastolic threshold surpassed
        if(bloodPressureReading > 120 || bloodPressureReading < 60){
            state = State.CRITICAL_ALERT;
            return;
        }
        checkTrend(diastolicReadings);
    }


    private void monitorSystolicPressure(PatientRecord record){
        double bloodPressureReading = record.getMeasurementValue();
        systolicReadings.add(bloodPressureReading);

        //systolic threshold surpassed
        if(bloodPressureReading > 180 || bloodPressureReading < 90){
            state = State.CRITICAL_ALERT;
            return;
        }
        checkTrend(systolicReadings);
    }

    private void checkTrend(List<Double> bloodPressureReading){
        int consecutiveCount = 0;
        int size = bloodPressureReading.size();;

        //no need to check trend if there are less than 3 data points
        if (size <= 2) {
            return;
        }

        //no need to iterate over the entire list everytime, only the last 3 values
        double earliestReading = bloodPressureReading.get(size - 3);
        double middleReading = bloodPressureReading.get(size - 2);
        double latestReading = bloodPressureReading.get(size - 1);

        double difference1 = middleReading - earliestReading;
        double difference2 = latestReading - middleReading;

        //identify a positive or negative trend
        if((difference1 > 10 && difference2 > 10) || (difference1 < -10 && difference2 < -10) ){
            state = State.TREND_ALERT;
        }


    }

    public Alert getAlert(){
        return alert;
    }

    public State getState(){
        return state;
    }

    public void resetState(){
        state = State.NORMAL;
    }
}
