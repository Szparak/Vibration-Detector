package com.example.jakpe.vibrationdetector;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by pernal on 10.12.17.
 */

public class DFTService extends IntentService {

    private double[] accelerationValuesInWindow;
    private double frequencySize;
    private double[] absDftValues;
    public static final String ACTION = "com.example.jakpe.vibrationdetector.DFTService";

    public DFTService() {
        super("DFT service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent broadcastIntent = new Intent(ACTION);
        accelerationValuesInWindow = intent.getDoubleArrayExtra("acceleration values");
        frequencySize = intent.getDoubleExtra("frequency size", 0 );
        double twoHighestFrequencies[];

        DFTCalculations dftCalculation = new DFTCalculations();
        dftCalculation.calculateDFT(accelerationValuesInWindow);
        absDftValues = dftCalculation.calculateAndGetAbsValueOfDFT();
        twoHighestFrequencies = dftCalculation.calculateTwoHighestFrequencies(frequencySize);


        broadcastIntent.putExtra("resultCode" , Activity.RESULT_OK);
        broadcastIntent.putExtra("frequency size", frequencySize);
        broadcastIntent.putExtra("acceleration values", absDftValues);
        broadcastIntent.putExtra("highest frequencies" , twoHighestFrequencies);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

    }
}
