/**
 Serwis odpowiedzialny za obliczanie DFT i jej parametrów
 **/
package com.example.jakpe.vibrationdetector.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.jakpe.vibrationdetector.DFTCalculations;


public class DFTService extends IntentService {

    // inicjalizacja pól klasy
    private double[] accelerationValuesInWindow;
    private double frequencySize;
    private double amplitudeForMaxFrequency;
    private double displacementAmplitudeForMaxFrequency;
    private double[] absDftValues;
    public static final String ACTION = "com.example.jakpe.vibrationdetector.services.DFTService";

    // konstruktor bezparametrowy
    public DFTService() {
        super("DFT service");
    }

    // główna metoda serwisu obsługująca logikę
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent broadcastIntent = new Intent(ACTION);
        accelerationValuesInWindow = intent.getDoubleArrayExtra("acceleration values");
        frequencySize = intent.getDoubleExtra("frequency size", 0 );
        double highestFrequency;
        double calculateHighestAccelerationInWindowTimeValue;
        double numberOfSamples;

        // wywoływanie metod w celu przeprowadzenia obliczeń
        DFTCalculations dftCalculation = new DFTCalculations();
        dftCalculation.calculateDFT(accelerationValuesInWindow);
        absDftValues = dftCalculation.calculateAndGetAbsValueOfDFT();
        highestFrequency = dftCalculation.calculateHighestFrequency(frequencySize);
        amplitudeForMaxFrequency = dftCalculation.getHighestAmplitude();
        displacementAmplitudeForMaxFrequency = dftCalculation.getHighestDisplacementAmplitude();
        calculateHighestAccelerationInWindowTimeValue = dftCalculation.calculateHighestAccelerationInWindowTime(accelerationValuesInWindow);
        numberOfSamples = dftCalculation.getNumberOfSamples();

        // wysłanie transmisji broadcast z parametrami
        broadcastIntent.putExtra("resultCode" , Activity.RESULT_OK);
        broadcastIntent.putExtra("frequency size", frequencySize);
        broadcastIntent.putExtra("acceleration values", absDftValues);
        broadcastIntent.putExtra("highest frequency" , highestFrequency);
        broadcastIntent.putExtra("amplitudeForMaxFrequency", amplitudeForMaxFrequency);
        broadcastIntent.putExtra("displacementAmplitudeForMaxFrequency" , displacementAmplitudeForMaxFrequency);
        broadcastIntent.putExtra("maxAccelerationInWindowTime", calculateHighestAccelerationInWindowTimeValue);
        broadcastIntent.putExtra("numberOfSamples", numberOfSamples);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

    }
}
