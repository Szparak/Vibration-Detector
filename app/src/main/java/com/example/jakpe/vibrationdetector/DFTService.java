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
    private int samplingFrequency;
    private double[] absDftvalues;
    public static final String ACTION = "com.example.jakpe.vibrationdetector.DFTService";

    public DFTService() {
        super("DFT service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Intent broadcastIntent = new Intent(ACTION);
        accelerationValuesInWindow = intent.getDoubleArrayExtra("acceleration values");
        samplingFrequency = intent.getIntExtra("sampling frequency", 0);

        DFTCalculations dftCalculation = new DFTCalculations();
        dftCalculation.calculateDFT(accelerationValuesInWindow);
        absDftvalues = dftCalculation.calculateAbsValueOfDFT();

        broadcastIntent.putExtra("resultCode" , Activity.RESULT_OK);
        broadcastIntent.putExtra("sampling frequency", samplingFrequency);
        broadcastIntent.putExtra("acceleration values", absDftvalues);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

    }
}
