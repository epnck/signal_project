package com.monitors;

import com.data_management.PatientRecord;

public interface AlertStrategy {
    void checkAlert(PatientRecord patientRecord);

};
