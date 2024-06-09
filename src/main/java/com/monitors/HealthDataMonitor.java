package com.monitors;

import com.data_management.PatientRecord;

public interface HealthDataMonitor {
    void validateData(PatientRecord patientRecord);

};
