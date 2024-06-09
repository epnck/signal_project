package com.monitors;

import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

public class BloodSaturationStrategy implements AlertStrategy {
    public enum State{
        NORMAL,
        LOW_SATURATION_ALERT,
        RAPID_DROP_ALERT,

    }
    private BloodSaturationStrategy.State state = BloodSaturationStrategy.State.NORMAL;

    List<PatientRecord> bloodSaturationRecords = new ArrayList<>();
    public BloodSaturationStrategy(){

    }

    @Override
    public void checkAlert(PatientRecord patientRecord) {
        //current data
        double currentBloodSaturation = patientRecord.getMeasurementValue();
        long currentTimestamp = patientRecord.getTimestamp();
        bloodSaturationRecords.add(patientRecord);


        //if blood saturation is below the threshold trigger an alert
        if(currentBloodSaturation < 92){
            state = State.LOW_SATURATION_ALERT;
            return;
        }

        //if blood saturation drops 5% or more in a 10-minute window, trigger an alert
        if(bloodSaturationRecords.size() >= 2){
            //previous data
            double previousBloodSaturation =  bloodSaturationRecords.get(bloodSaturationRecords.size() -2).getMeasurementValue();
            long previousTimestamp =  bloodSaturationRecords.get(bloodSaturationRecords.size() -2).getTimestamp();


            if(currentBloodSaturation - previousBloodSaturation <= -5 && currentTimestamp - previousTimestamp <= 600000){ //time in milliseconds so 600000-milliseconds is 10-minutes
               state = State.RAPID_DROP_ALERT;
            }
        }
    }

    public BloodSaturationStrategy.State getState(){
        return state;
    }

    public void resetState(){
        state = BloodSaturationStrategy.State.NORMAL;
    }
}


