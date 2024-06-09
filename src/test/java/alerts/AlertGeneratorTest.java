package alerts;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.Assert;
import org.junit.Test;


public class AlertGeneratorTest {

    @Test
    public void checkTrendBloodPressure(){
        var mock = new AlertGenerator(new DataStorage());

        //initialize test patients
        Patient readingsSystolicIncrease = new Patient(1);
        Patient readingsSystolicDecrease = new Patient(2);
        Patient readingsSystolicNormal = new Patient(3);
        Patient readingsDiastolicIncrease = new Patient(4);
        Patient readingsDiastolicDecrease = new Patient(5);
        Patient readingsDiastolicNormal = new Patient(6);
        Patient readingsMixed = new Patient(7);

        //add systolic pressure increase records
        readingsSystolicIncrease.addRecord(110, "SystolicPressure", System.currentTimeMillis());
        readingsSystolicIncrease.addRecord(121, "SystolicPressure", System.currentTimeMillis());
        readingsSystolicIncrease.addRecord(132, "SystolicPressure", System.currentTimeMillis());

        //add systolic pressure decrease records
        readingsSystolicDecrease.addRecord(130, "SystolicPressure", System.currentTimeMillis());
        readingsSystolicDecrease.addRecord(119, "SystolicPressure", System.currentTimeMillis());
        readingsSystolicDecrease.addRecord(108, "SystolicPressure", System.currentTimeMillis());

        //add systolic pressure normal records
        readingsSystolicNormal.addRecord(110, "SystolicPressure", System.currentTimeMillis());
        readingsSystolicNormal.addRecord(120, "SystolicPressure", System.currentTimeMillis());
        readingsSystolicNormal.addRecord(110, "SystolicPressure", System.currentTimeMillis());

        //add diastolic pressure increase records
        readingsDiastolicIncrease.addRecord(90, "DiastolicPressure", System.currentTimeMillis());
        readingsDiastolicIncrease.addRecord(101, "DiastolicPressure", System.currentTimeMillis());
        readingsDiastolicIncrease.addRecord(112, "DiastolicPressure", System.currentTimeMillis());

        //add diastolic pressure decrease records
        readingsDiastolicDecrease.addRecord(110, "DiastolicPressure", System.currentTimeMillis());
        readingsDiastolicDecrease.addRecord(99, "DiastolicPressure", System.currentTimeMillis());
        readingsDiastolicDecrease.addRecord(88, "DiastolicPressure", System.currentTimeMillis());

        //add diastolic pressure normal records
        readingsDiastolicNormal.addRecord(100, "DiastolicPressure", System.currentTimeMillis());
        readingsDiastolicNormal.addRecord(110, "DiastolicPressure", System.currentTimeMillis());
        readingsDiastolicNormal.addRecord(100, "DiastolicPressure", System.currentTimeMillis());

        //add mixed pressure records
        readingsMixed.addRecord(89, "DiastolicPressure", System.currentTimeMillis());
        readingsMixed.addRecord(100, "SystolicPressure", System.currentTimeMillis());
        readingsMixed.addRecord(111, "DiastolicPressure", System.currentTimeMillis());

        //test and evaluate all scenarios
        mock.evaluateData(readingsSystolicIncrease);
        Assert.assertTrue(mock.bloodPressureTriggerd);

        mock.evaluateData(readingsSystolicDecrease);
        Assert.assertTrue(mock.bloodPressureTriggerd);

        mock.evaluateData(readingsSystolicNormal);
        Assert.assertFalse(mock.bloodPressureTriggerd);

        mock.evaluateData(readingsDiastolicIncrease);
        Assert.assertTrue(mock.bloodPressureTriggerd);

        mock.evaluateData(readingsDiastolicDecrease);
        Assert.assertTrue(mock.bloodPressureTriggerd);

        mock.evaluateData(readingsDiastolicNormal);
        Assert.assertFalse(mock.bloodPressureTriggerd);

        mock.evaluateData(readingsMixed);
        Assert.assertFalse(mock.bloodPressureTriggerd);
    }
    @Test
    public void checkCriticalBloodpressure(){
        var mock = new AlertGenerator(new DataStorage());
        Patient systolicIncrease = new Patient(1);
        Patient systolicDecrease = new Patient(2);
        Patient diastolicIncrease = new Patient(3);
        Patient diastolicDecrease = new Patient(4);
        Patient diastolicNormal = new Patient(5);
        Patient systolicNormal = new Patient(6);


        //add records to different patients
        systolicIncrease.addRecord(190, "SystolicPressure", System.currentTimeMillis());
        systolicDecrease.addRecord(80, "SystolicPressure", System.currentTimeMillis());

        diastolicIncrease.addRecord(130, "DiastolicPressure", System.currentTimeMillis());
        diastolicDecrease.addRecord(50, "DiastolicPressure", System.currentTimeMillis());

        diastolicNormal.addRecord(100, "DiastolicPressure", System.currentTimeMillis());
        systolicNormal.addRecord(110, "SystolicPressure", System.currentTimeMillis());

        //test and evaluate all cases
        mock.evaluateData(systolicIncrease);
        Assert.assertTrue(mock.bloodPressureTriggerd);

        mock.evaluateData(systolicDecrease);
        Assert.assertTrue(mock.bloodPressureTriggerd);

        mock.evaluateData(diastolicIncrease);
        Assert.assertTrue(mock.bloodPressureTriggerd);

        mock.evaluateData(diastolicDecrease);
        Assert.assertTrue(mock.bloodPressureTriggerd);

        mock.evaluateData(systolicNormal);
        Assert.assertFalse(mock.bloodPressureTriggerd);

        mock.evaluateData(diastolicNormal);
        Assert.assertFalse(mock.bloodPressureTriggerd);
    }
    @Test
    public void checkBloodSaturation(){
        var mock = new AlertGenerator(new DataStorage());
        Patient normal = new Patient(1);
        Patient belowThreshold = new Patient(2);
        Patient edgeCase = new Patient(3);

        normal.addRecord(94, "BloodSaturation", System.currentTimeMillis());
        belowThreshold.addRecord(88, "BloodSaturation", System.currentTimeMillis());
        edgeCase.addRecord(92, "BloodSaturation", System.currentTimeMillis());

        //test and evaluate all cases
        mock.evaluateData(normal);
        Assert.assertFalse(mock.bloodSaturationTriggered);

        mock.evaluateData(belowThreshold);
        Assert.assertTrue(mock.bloodSaturationTriggered);

        mock.evaluateData(edgeCase);
        Assert.assertFalse(mock.bloodSaturationTriggered);
    }
    @Test
    public void checkBloodSaturationRapidDrop(){
        var mock = new AlertGenerator(new DataStorage());
        Patient patient1 = new Patient(1);
        Patient patient2 = new Patient(2);
        Patient patient3 = new Patient(3);

        //rapid drop within timeframe
        patient1.addRecord(99, "BloodSaturation", System.currentTimeMillis() - 500000);
        patient1.addRecord(93, "BloodSaturation", System.currentTimeMillis());

        //rapid drop outside timeframe
        patient2.addRecord(99, "BloodSaturation", System.currentTimeMillis() - 700000);
        patient2.addRecord(93, "BloodSaturation", System.currentTimeMillis());

        //normal data within timeframe
        patient3.addRecord(99, "BloodSaturation", System.currentTimeMillis() - 500000);
        patient3.addRecord(97, "BloodSaturation", System.currentTimeMillis());

        mock.evaluateData(patient1);
        Assert.assertTrue(mock.bloodSaturationTriggered);

        mock.evaluateData(patient2);
        Assert.assertFalse(mock.bloodSaturationTriggered);

        mock.evaluateData(patient3);
        Assert.assertFalse(mock.bloodSaturationTriggered);
    }
    @Test
    public void checkHypotensiveHypoxemiaAlert(){
        var mock = new AlertGenerator(new DataStorage());
        Patient patient1 = new Patient(1);
        Patient patient2 = new Patient(2);
        Patient patient3 = new Patient(3);

        //both values are below threshold
        patient1.addRecord(80, "SystolicPressure", System.currentTimeMillis());
        patient1.addRecord(90, "BloodSaturation", System.currentTimeMillis());

        //blood saturation is below threshold, blood pressure is normal
        patient2.addRecord(100, "SystolicPressure", System.currentTimeMillis());
        patient2.addRecord(91, "BloodSaturation", System.currentTimeMillis());

        //blood pressure is below threshold, blood saturation is normal
        patient3.addRecord(80, "SystolicPressure", System.currentTimeMillis());
        patient3.addRecord(95, "BloodSaturation", System.currentTimeMillis());

        mock.evaluateData(patient1);
        Assert.assertTrue(mock.hypoTriggered);

        mock.evaluateData(patient2);
        Assert.assertFalse(mock.hypoTriggered);

        mock.evaluateData(patient3);
        Assert.assertFalse(mock.hypoTriggered);
    }
    @Test
    public void checkEcg(){
        var mock = new AlertGenerator(new DataStorage());
        Patient patient = new Patient(1);

        patient.addRecord(85, "ECG", System.currentTimeMillis());
        patient.addRecord(91, "ECG", System.currentTimeMillis());
        patient.addRecord(95, "ECG", System.currentTimeMillis());
        //more than 15% deviation from average
        patient.addRecord(120, "ECG", System.currentTimeMillis());

        mock.evaluateData(patient);
        Assert.assertTrue(mock.ecgTriggerd);
    }
}
