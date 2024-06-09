package com.alerts.decorators;


public class AlertDecorator implements AlertInterface {
    protected AlertInterface decoratedAlert;

    public AlertDecorator(AlertInterface decoratedAlert) {
        this.decoratedAlert = decoratedAlert;
    }

    @Override
    public void sendAlert() {
        decoratedAlert.sendAlert();
    }
}