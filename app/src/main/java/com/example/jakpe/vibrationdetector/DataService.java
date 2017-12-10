package com.example.jakpe.vibrationdetector;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * Created by pernal on 09.12.17.
 */

public class DataService extends IntentService implements SensorEventListener {
    public static volatile boolean isStopped;
    private double accelerationValue=0;
    double gravity=0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final int samplingFrequency = 50;
    long timeOnBeggining, timeOnEnd;
    private int windowTimeinMilis;
    public static final String ACTION = "com.example.jakpe.vibrationdetector.DataService";
    private String axis=null;
    private double[] accelerationValuesInWindow;


    public DataService() {
        super("Data service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isStopped = false;
        setupSensor();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        axis = intent.getStringExtra("axis");
        windowTimeinMilis = intent.getIntExtra("Window Time" , 0) * 1000;

        int iteration =0;
        int samplingInMilis = 1000/samplingFrequency;
        accelerationValuesInWindow = new double[windowTimeinMilis/samplingInMilis];
        Intent broadcastIntent = new Intent(ACTION);

        while(!isStopped){

            try {
                if((timeOnEnd-timeOnBeggining) < samplingInMilis)
                    Thread.sleep((long) samplingInMilis - (timeOnEnd-timeOnBeggining));
                else
                    Thread.sleep((long) samplingInMilis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeOnBeggining = SystemClock.currentThreadTimeMillis();

            if(iteration == accelerationValuesInWindow.length-1){
                startDFTService(accelerationValuesInWindow);
                iteration=0;
            }

            accelerationValuesInWindow[iteration] = accelerationValue;

            broadcastIntent.putExtra("resultCode" , Activity.RESULT_OK);
            broadcastIntent.putExtra("resultValue" , accelerationValue);
            broadcastIntent.putExtra("axis" , axis);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
            iteration++;
            timeOnEnd = SystemClock.currentThreadTimeMillis();
        }
    }

    private void startDFTService(double[] valuesArray){
        Intent dftIntent = new Intent(this , DFTService.class);
        dftIntent.putExtra("acceleration values" , valuesArray);
        dftIntent.putExtra("sampling frequency" , samplingFrequency);
        startService(dftIntent);
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
        if(axis!=null){
            switch(axis){
                case "X":
                    gravity = alpha * gravity + (1 - alpha) * sensorEvent.values[0];
                    accelerationValue=sensorEvent.values[0] - gravity;
                    break;
                case "Y":
                    gravity = alpha * gravity + (1 - alpha) * sensorEvent.values[1];
                    accelerationValue=sensorEvent.values[1] - gravity;
                    break;
                case "Z":
                    gravity = alpha * gravity + (1 - alpha) * sensorEvent.values[2];
                    accelerationValue=sensorEvent.values[2] - gravity;
                    break;
            }
        }

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
