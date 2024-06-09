package com.monitors;

import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class BloodSaturationMonitor implements HealthDataMonitor{
    public enum State{
        NORMAL,
        LOW_SATURATION_ALERT,
        RAPID_DROP_ALERT,

    }
    private BloodSaturationMonitor.State state = BloodSaturationMonitor.State.NORMAL;

    List<PatientRecord> bloodSaturationRecords = new ArrayList<>();
    public BloodSaturationMonitor(){

    }

    @Override
    public void validateData(PatientRecord patientRecord) {
        //current data
        double currentBloodSaturation = patientRecord.getMeasurementValue();
        long currentTimestamp = patientRecord.getTimestamp();
        bloodSaturationRecords.add(patientRecord);

        //previous data
        double previousBloodSaturation =  bloodSaturationRecords.get(bloodSaturationRecords.size() -2).getMeasurementValue();
        long previousTimestamp =  bloodSaturationRecords.get(bloodSaturationRecords.size() -2).getTimestamp();

        //if blood saturation is below the threshold trigger an alert
        if(currentBloodSaturation < 92){
            state = State.LOW_SATURATION_ALERT;
            return;
        }

        //if blood saturation drops 5% or more in a 10-minute window, trigger an alert
        if(bloodSaturationRecords.size() >= 2){
            if(currentBloodSaturation - previousBloodSaturation <= -5 && currentTimestamp - previousTimestamp <= 10){
               state = State.RAPID_DROP_ALERT;
            }
        }
    }

    public BloodSaturationMonitor.State getState(){
        return state;
    }

    public void resetState(){
        state = BloodSaturationMonitor.State.NORMAL;
    }
}


