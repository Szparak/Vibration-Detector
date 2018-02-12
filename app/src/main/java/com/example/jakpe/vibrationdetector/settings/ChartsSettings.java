package com.example.jakpe.vibrationdetector.settings;

import android.content.SharedPreferences;

/**
 * Created by Pernal on 2/12/2018.
 */

public class ChartsSettings {

    private static boolean gravityForce;
    private static int samplingValue;
    private static int windowTimeValue;


    public static boolean getGravityForce() {
        return gravityForce;
    }

    public static void setGravityForce(boolean gravityForce) {
        ChartsSettings.gravityForce = gravityForce;
    }

    public static int getSampligValue() {
        return samplingValue;
    }

    public static void setSampligValue(int sampligValue) {
        ChartsSettings.samplingValue = sampligValue;
    }

    public static int getWindowTimeValue() {
        return windowTimeValue;
    }

    public static void setWindowTimeValue(int windowTimeValue) {
        ChartsSettings.windowTimeValue = windowTimeValue;
    }
}
