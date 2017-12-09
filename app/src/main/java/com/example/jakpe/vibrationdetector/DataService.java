package com.example.jakpe.vibrationdetector;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * Created by pernal on 09.12.17.
 */

public class DataService extends IntentService implements SensorEventListener {
    public volatile boolean isStopped = false;
    private double xAxisValue=0;
    double gravityX;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final int samplingFrequency = 50;
    private double xAxisTime;
    public static final String ACTION = "com.example.jakpe.vibrationdetector.DataService";


    public DataService() {
        super("Data service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        xAxisTime=0;
        setupSensor();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Bundle bundle = new Bundle();

        double samplingInMilis = (double) 1/samplingFrequency*1000;
        double timeWectorInMilis = (double) 1/samplingFrequency;
        Intent broadcastIntent = new Intent(ACTION);

        while(!isStopped){
            try {
                Thread.sleep((long) samplingInMilis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            broadcastIntent.putExtra("resultCode" , Activity.RESULT_OK);
            broadcastIntent.putExtra("resultValue" , xAxisValue);
            broadcastIntent.putExtra("xAxisTime", xAxisTime);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            xAxisTime += timeWectorInMilis;
        }
    }

    void setupSensor(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = null;

        // Use the accelerometer.
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        else{
            Toast.makeText(this, "no accelerometer on phone" , Toast.LENGTH_SHORT).show();
        }

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        final double alpha = 0.8;
        // Isolate the force of gravity with the low-pass filter.
        gravityX = alpha * gravityX + (1 - alpha) * sensorEvent.values[0];

        xAxisValue=sensorEvent.values[0] - gravityX;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }
}
