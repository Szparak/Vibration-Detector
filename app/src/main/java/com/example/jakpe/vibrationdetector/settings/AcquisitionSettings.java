/**
 Klasa odpowiedzialna za przetrzymywanie globalnych ustawień
 **/
package com.example.jakpe.vibrationdetector.settings;

public class AcquisitionSettings {

    // inicjalizacja pól klasy
    private static String fileName;
    private static String description;
    private static int samplingFrequency;
    private static int measurementTime;
    public static int fileCounter;

    // metoda ustawiająca licznik plików
    public static void setFileCounter(int fileCounter) {
        AcquisitionSettings.fileCounter = fileCounter;
    }

    // metoda zwracająca nazwę pliku
    public static String getFileName() {
        return fileName;
    }
    // metoda ustawiająca nazwę pliku
    public static void setFileName(String fileName) {
        AcquisitionSettings.fileName = fileName;
    }

    // metoda zwracająca opis pomiaru
    public static String getDescription() {
        return description;
    }

    // metoda ustawiająca opis pomiaru
    public static void setDescription(String description) {
        AcquisitionSettings.description = description;
    }

    // metoda zwracająca częstotliwość próbkowania
    public static int getSamplingFrequency() {
        return samplingFrequency;
    }

    // metoda ustawiająca częstotliwość próbkowania
    public static void setSamplingFrequency(int samplingFrequency) {
        AcquisitionSettings.samplingFrequency = samplingFrequency;
    }

    // metoda zwracająca czas pomiaru
    public static int getMeasurementTime() {
        return measurementTime;
    }

    // metoda zwracająca czas pomiaru
    public static void setMeasurementTime(int measurementTime) {
        AcquisitionSettings.measurementTime = measurementTime;
    }
}
