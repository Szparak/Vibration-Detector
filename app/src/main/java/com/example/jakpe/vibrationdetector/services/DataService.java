package com.example.jakpe.vibrationdetector.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.example.jakpe.vibrationdetector.FileSaver;
import com.example.jakpe.vibrationdetector.services.DFTService;
import com.example.jakpe.vibrationdetector.settings.AcquisitionSettings;
import com.example.jakpe.vibrationdetector.settings.ChartsSettings;

import java.io.IOException;

/**
 * Created by pernal on 09.12.17.
 */

public class DataService extends IntentService implements SensorEventListener {
    public static volatile boolean isStopped;
    private double accelerationValueX=0;
    private double accelerationValueY=0;
    private double accelerationValueZ=0;
    double gravityX, gravityY, gravityZ;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    public static final String ACTION = "com.example.jakpe.vibrationdetector.services.DataService";
    private double[] accelerationValuesInWindow;
    String axis;
    private int acquisitionSamplingFreq;
    private int measurementTime;
    private int samplingFrequency;
    private int analysisWindowTime;
    long samplingInMillis;
    boolean gravityForceFilter;
    final double alpha = 0.8;
    public static boolean writingMode;
    private double[] acquisitionXTable;
    private double[] acquisitionYTable;
    private double[] acquisitionZTable;


    public DataService() {
        super("Data service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isStopped = false;
        writingMode = false;
        setupSensor();
        gravityX=0;
        gravityY=0;
        gravityZ=0;
        acquisitionSamplingFreq = AcquisitionSettings.getSamplingFrequency(); //do podpiecia
        measurementTime = AcquisitionSettings.getMeasurementTime(); //do podpiecia
        samplingFrequency = ChartsSettings.getSampligValue();
        analysisWindowTime = ChartsSettings.getWindowTimeValue();
        gravityForceFilter = ChartsSettings.getGravityForce();
        samplingInMillis = 1000/samplingFrequency;
        acquisitionXTable=new double[measurementTime*acquisitionSamplingFreq];
        acquisitionYTable=new double[measurementTime*acquisitionSamplingFreq];
        acquisitionZTable=new double[measurementTime*acquisitionSamplingFreq];
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(!writingMode){
            axis = intent.getStringExtra("axis");
            String analysisMode = intent.getStringExtra("Analysis Mode");

            int iteration =0;
            accelerationValuesInWindow = new double[analysisWindowTime*samplingFrequency];
            Intent broadcastIntent = new Intent(ACTION);
            broadcastIntent.putExtra("resultCode" , Activity.RESULT_OK);
            double frequencySize =(double) samplingFrequency / accelerationValuesInWindow.length ;

            while(!isStopped){

                try {
                    Thread.sleep(samplingInMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
            }
        }
        if(writingMode){


            gravityX=0;
            gravityY=0;
            gravityZ=0;

            for(int i=0; i<acquisitionXTable.length; i++){

                acquisitionXTable[i]=accelerationValueX;
                acquisitionYTable[i]=accelerationValueY;
                acquisitionZTable[i]=accelerationValueZ;

                try {
                    Thread.sleep(1000/acquisitionSamplingFreq);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            writingMode=false;

            new Thread(() -> {
                try {
                    FileSaver.saveData(acquisitionXTable, acquisitionYTable, acquisitionZTable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
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
            FileSaver.sensor = mSensor;
        }
        else{
            Toast.makeText(this, "no accelerometer on phone" , Toast.LENGTH_SHORT).show();
        }

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        if(gravityForceFilter && !writingMode){
            gravityX = alpha * gravityX + (1 - alpha) * sensorEvent.values[0];
            gravityY = alpha * gravityY + (1 - alpha) * sensorEvent.values[1];
            gravityZ = alpha * gravityZ + (1 - alpha) * sensorEvent.values[2];
        }

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
