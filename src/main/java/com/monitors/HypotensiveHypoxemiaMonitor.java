package com.monitors;

import com.data_management.PatientRecord;



public class HypotensiveHypoxemiaMonitor implements HealthDataMonitor {
    public enum State{
        NORMAL,
        HH_ALERT,

    }
    private State state = State.NORMAL;
    private double currentSystolicPressure = -1;
    private double currentBloodSaturation = -1;

    public HypotensiveHypoxemiaMonitor(){

    }

    @Override
    public void validateData(PatientRecord patientRecord) {
        if(patientRecord.getRecordType().equals("SystolicPressure")){
            currentSystolicPressure = patientRecord.getMeasurementValue();
        }else{
            currentBloodSaturation = patientRecord.getMeasurementValue();
        }

        if(currentSystolicPressure != -1 && currentSystolicPressure < 90 &&
                currentBloodSaturation != -1 && currentBloodSaturation < 92){
            state = State.HH_ALERT;
        }

    }

    public State getState(){
        return state;
    }

    public void resetState(){
        state = State.NORMAL;
    }
}
