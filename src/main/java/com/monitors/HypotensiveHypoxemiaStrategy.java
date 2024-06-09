package com.monitors;

import com.alerts.Alert;
import com.alerts.alert_factories.HypotensiveHypoxemiaAlertFactory;
import com.data_management.PatientRecord;



public class HypotensiveHypoxemiaStrategy implements AlertStrategy {
    public enum State{
        NORMAL,
        HH_ALERT,

    }
    private State state = State.NORMAL;
    private double currentSystolicPressure = -1;
    private double currentBloodSaturation = -1;
    private Alert alert;

    public HypotensiveHypoxemiaStrategy(){

    }

    @Override
    public boolean checkAlert(PatientRecord patientRecord) {
        //reset alert for each time method is called
        alert = null;

        if(patientRecord.getRecordType().equals("SystolicPressure")){
            currentSystolicPressure = patientRecord.getMeasurementValue();
        }else{
            currentBloodSaturation = patientRecord.getMeasurementValue();
        }

        //if the current values are assigned and below the threshold, update the state
        if(currentSystolicPressure != -1 && currentSystolicPressure < 90 &&
                currentBloodSaturation != -1 && currentBloodSaturation < 92){
            state = State.HH_ALERT;
        }

        //create an alert
        if(state == State.HH_ALERT){
            alert = new HypotensiveHypoxemiaAlertFactory().createAlert(Integer.toString(patientRecord.getPatientId()),
                    "Both BP and Saturation below Threshold", patientRecord.getTimestamp());
            resetState();
            return true;
        }

        return false;
    }

    public State getState(){
        return state;
    }

    public Alert getAlert(){
        return alert;
    }

    public void resetState(){
        state = State.NORMAL;
    }
}
