package com.alerts.decorators;

import com.alerts.Alert;

public class BasicAlert implements AlertInterface {
    private Alert alert;

    public BasicAlert(Alert alert) {
        this.alert = alert;
    }

    @Override
    public void sendAlert() {
        System.out.println("Alert for patient: " + alert.getPatientId() +
                ", Condition: " + alert.getCondition() +
                ", Timestamp: " + alert.getTimestamp());
    }
}
