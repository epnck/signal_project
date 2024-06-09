package com.monitors;

import com.data_management.PatientRecord;

public interface AlertStrategy {
    boolean checkAlert(PatientRecord patientRecord);

};
