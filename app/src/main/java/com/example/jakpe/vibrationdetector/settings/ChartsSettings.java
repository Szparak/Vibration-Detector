/**
 Klasa odpowiedzialna za przetrzymywanie globalnych ustawień
 **/
package com.example.jakpe.vibrationdetector.settings;


public class ChartsSettings {

    // inicjalizacja pól klasy
    private static boolean gravityForce;
    private static int samplingValue;
    private static int windowTimeValue;


    // metoda zwracająca flage filtra grawitacji
    public static boolean getGravityForce() {
        return gravityForce;
    }

    // metoda ustawiająca flage filtra grawitacji
    public static void setGravityForce(boolean gravityForce) {
        ChartsSettings.gravityForce = gravityForce;
    }

    // metoda zwracająca częstotliwość próbkowania
    public static int getSampligValue() {
        return samplingValue;
    }

    // metoda ustawiająca częstotliwość próbkowania
    public static void setSampligValue(int sampligValue) {
        ChartsSettings.samplingValue = sampligValue;
    }

    // metoda zwracająca szerokość okna czasowego
    public static int getWindowTimeValue() {
        return windowTimeValue;
    }

    // metoda ustawiająca szerokość okna czasowego
    public static void setWindowTimeValue(int windowTimeValue) {
        ChartsSettings.windowTimeValue = windowTimeValue;
    }
}
