package com.example.jakpe.vibrationdetector.settings;

/**
 * Created by Pernal on 2/12/2018.
 */

public class AcquisitionSettings {

    private static String fileName;
    private static String description;
    private static int samplingFrequency;
    private static int measurementTime;
    public static int fileCounter;

    public static void setFileCounter(int fileCounter) {
        AcquisitionSettings.fileCounter = fileCounter;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        AcquisitionSettings.fileName = fileName;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        AcquisitionSettings.description = description;
    }

    public static int getSamplingFrequency() {
        return samplingFrequency;
    }

    public static void setSamplingFrequency(int samplingFrequency) {
        AcquisitionSettings.samplingFrequency = samplingFrequency;
    }

    public static int getMeasurementTime() {
        return measurementTime;
    }

    public static void setMeasurementTime(int measurementTime) {
        AcquisitionSettings.measurementTime = measurementTime;
    }
}
