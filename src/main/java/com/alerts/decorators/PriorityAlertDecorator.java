package com.alerts.decorators;

public class PriorityAlertDecorator extends AlertDecorator {
    private final String priorityLevel;

    public PriorityAlertDecorator(AlertInterface decoratedAlert, String priorityLevel) {
        super(decoratedAlert);
        this.priorityLevel = priorityLevel;
    }

    @Override
    public void sendAlert() {
        System.out.println("Priority: " + priorityLevel);
        super.sendAlert();
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }
}
