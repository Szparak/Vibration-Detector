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
    private double accelerationValueX=0;
    private double accelerationValueY=0;
    private double accelerationValueZ=0;
    double gravityX=0, gravityY=0, gravityZ=0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final int samplingInMilis = 20;
    long timeOnBeggining, timeOnEnd;
    private int windowTime;
    public static final String ACTION = "com.example.jakpe.vibrationdetector.DataService";
    private double[] accelerationValuesInWindow;
    private long readTime=0, readTemp=0;
    String axis;


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

        windowTime = intent.getIntExtra("Window Time" , 0);
        axis = intent.getStringExtra("axis");
        String analysisMode = intent.getStringExtra("Analysis Mode");

        int iteration =0;
        int samplingFrequency = 1000/samplingInMilis;
        accelerationValuesInWindow = new double[windowTime*samplingFrequency];
        Intent broadcastIntent = new Intent(ACTION);
        broadcastIntent.putExtra("resultCode" , Activity.RESULT_OK);
        double frequencySize =(double) samplingFrequency / accelerationValuesInWindow.length ;

        while(!isStopped){

            try {
                    Thread.sleep((long) samplingInMilis -(timeOnEnd-timeOnBeggining));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeOnBeggining = SystemClock.currentThreadTimeMillis();

            if(analysisMode!=null && analysisMode.equals("ON")){
                if(iteration == accelerationValuesInWindow.length-1){
                    new Thread(() -> startDFTService(accelerationValuesInWindow, frequencySize)).start();
                    iteration=0;
                }

                putValueIntoAxisArray(axis, iteration);
                iteration++;
            }

            broadcastIntent.putExtra("resultValueX" , accelerationValueX);
            broadcastIntent.putExtra("resultValueY" , accelerationValueY);
            broadcastIntent.putExtra("resultValueZ" , accelerationValueZ);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

            timeOnEnd = SystemClock.currentThreadTimeMillis();
        }
    }

    private void startDFTService(double[] valuesArray, double frequencySize){
        Intent dftIntent = new Intent(this , DFTService.class);
        dftIntent.putExtra("acceleration values" , valuesArray);
        dftIntent.putExtra("frequency size" , frequencySize);
        startService(dftIntent);
    }

    private void putValueIntoAxisArray(String axis, int iteration){

        if(axis.equals("X"))
                accelerationValuesInWindow[iteration] = accelerationValueX;
        if(axis.equals("Y"))
                accelerationValuesInWindow[iteration] = accelerationValueY;
        if(axis.equals("Z"))
                accelerationValuesInWindow[iteration] = accelerationValueZ;

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
        //póki co bez filtracji, w ustawieniach bedzie opcja wyboru?? moze...


        final double alpha = 0.8;
        gravityX = alpha * gravityX + (1 - alpha) * sensorEvent.values[0];
        gravityY = alpha * gravityY + (1 - alpha) * sensorEvent.values[1];
        gravityZ = alpha * gravityZ + (1 - alpha) * sensorEvent.values[2];

        accelerationValueX=sensorEvent.values[0] - gravityX;
        accelerationValueY=sensorEvent.values[1] - gravityY;
        accelerationValueZ=sensorEvent.values[2] - gravityZ;

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
