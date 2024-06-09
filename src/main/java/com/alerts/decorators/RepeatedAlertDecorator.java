package com.alerts.decorators;

import java.util.Timer;
import java.util.TimerTask;

public class RepeatedAlertDecorator extends AlertDecorator {
    private final long interval;

    public RepeatedAlertDecorator(AlertInterface decoratedAlert, long interval) {
        super(decoratedAlert);
        this.interval = interval;
    }

    @Override
    public void sendAlert() {
        super.sendAlert();
        //starts a timer, while timer is within the interval alerts are sent
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                decoratedAlert.sendAlert();
            }
        }, interval, interval);
    }
}
