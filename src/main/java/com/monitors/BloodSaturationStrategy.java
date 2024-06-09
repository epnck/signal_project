package com.monitors;

import com.alerts.Alert;
import com.alerts.alert_factories.BloodSaturationAlertFactory;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class BloodSaturationStrategy implements AlertStrategy {
    public enum State{
        NORMAL,
        LOW_SATURATION_ALERT,
        RAPID_DROP_ALERT,

    }
    private Alert alert;
    private BloodSaturationStrategy.State state = BloodSaturationStrategy.State.NORMAL;

    List<PatientRecord> bloodSaturationRecords = new ArrayList<>();
    public BloodSaturationStrategy(){

    }

    @Override
    public boolean checkAlert(PatientRecord patientRecord) {
        //reset alert for each time method is called
        alert = null;

        //current data
        double currentBloodSaturation = patientRecord.getMeasurementValue();
        long currentTimestamp = patientRecord.getTimestamp();
        bloodSaturationRecords.add(patientRecord);


        //if blood saturation is below the threshold trigger an alert
        if(currentBloodSaturation < 92){
            state = State.LOW_SATURATION_ALERT;
        }

        //there need to be at least 2 records to check for drop in saturation
        if(bloodSaturationRecords.size() >= 2){
            //previous data
            double previousBloodSaturation =  bloodSaturationRecords.get(bloodSaturationRecords.size() -2).getMeasurementValue();
            long previousTimestamp =  bloodSaturationRecords.get(bloodSaturationRecords.size() -2).getTimestamp();
            //if blood saturation drops 5% or more in a 10-minute window, trigger an alert
            if(currentBloodSaturation - previousBloodSaturation <= -5 && currentTimestamp - previousTimestamp <= 600000){ //time in milliseconds so 600000-milliseconds is 10-minutes
               state = State.RAPID_DROP_ALERT;
            }
        }

        if(state == State.LOW_SATURATION_ALERT){
            alert = new BloodSaturationAlertFactory().createAlert(Integer.toString(patientRecord.getPatientId()),
                    "Low Saturation", patientRecord.getTimestamp());
            resetState();
            return true;
        } else if (state == State.RAPID_DROP_ALERT) {
            alert = new BloodSaturationAlertFactory().createAlert(Integer.toString(patientRecord.getPatientId()),
                    "Rapid Drop", patientRecord.getTimestamp());
            resetState();
            return true;
        }
        //no abnormalities found
        return false;
    }

    public BloodSaturationStrategy.State getState(){
        return state;
    }

    public void resetState(){
        state = BloodSaturationStrategy.State.NORMAL;
    }

    public Alert getAlert(){
        return alert;
    }
}


