/**
 Serwis odpowiedzialny za czytanie danych z akcelerometru,
 wysyłanie ich transmisją broadcast a także za całą logikę
 zapisu danych i ich filtracji
 **/
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
import com.example.jakpe.vibrationdetector.settings.AcquisitionSettings;
import com.example.jakpe.vibrationdetector.settings.ChartsSettings;

import java.io.IOException;

import lombok.val;


public class DataService extends IntentService implements SensorEventListener {

    // inicjaliacja pól klasy
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


    // Konstruktor bezargumentowy
    public DataService() {
        super("Data service");
    }

    // metoda cyklu życia serwisu uruchamiana przy jego starcie
    @Override
    public void onCreate() {
        super.onCreate();
        val chartsSettings = ChartsSettings.getChartsSettings();
        val acquisitionSettings = AcquisitionSettings.getAcquisitionSettings();
        isStopped = false;
        writingMode = false;
        setupSensor();
        gravityX=0;
        gravityY=0;
        gravityZ=0;
        acquisitionSamplingFreq = acquisitionSettings.getSamplingFrequency();
        measurementTime = acquisitionSettings.getMeasurementTime();
        samplingFrequency = chartsSettings.getSamplingValue();
        analysisWindowTime = chartsSettings.getWindowTimeValue();
        gravityForceFilter = chartsSettings.isGravityForce();
        samplingInMillis = 1000/samplingFrequency;
        acquisitionXTable=new double[measurementTime*acquisitionSamplingFreq];
        acquisitionYTable=new double[measurementTime*acquisitionSamplingFreq];
        acquisitionZTable=new double[measurementTime*acquisitionSamplingFreq];
    }

    // główna metoda serwisu obsługująca logikę
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

            // pętla rozsyłania danych
            while(!isStopped){

                // realizacja próbkowania
                try {
                    Thread.sleep(samplingInMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // realizacja zapisu danych w oknie czasowym
                // i start serwisu realizującego ich przetwarzanie
                if(analysisMode!=null && analysisMode.equals("ON")){
                    if(iteration == accelerationValuesInWindow.length-1){
                        new Thread(() -> startDFTService(accelerationValuesInWindow, frequencySize)).start();
                        iteration=0;
                    }

                    putValueIntoAxisArray(axis, iteration);
                    iteration++;
                }

                // rozsyłanie danych
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

            // pętla akwizycji danych do pliku
            for(int i=0; i<acquisitionXTable.length; i++){

                acquisitionXTable[i]=accelerationValueX;
                acquisitionYTable[i]=accelerationValueY;
                acquisitionZTable[i]=accelerationValueZ;

                // realizacja próbkowania
                try {
                    Thread.sleep(1000/acquisitionSamplingFreq);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            writingMode=false;

            // wątek zapisujący dane do pliku
            new Thread(() -> {
                try {
                    FileSaver.saveData(acquisitionXTable, acquisitionYTable, acquisitionZTable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }

    // metoda startująca serwis DFT
    private void startDFTService(double[] valuesArray, double frequencySize){
        Intent dftIntent = new Intent(this , DFTService.class);
        dftIntent.putExtra("acceleration values" , valuesArray);
        dftIntent.putExtra("frequency size" , frequencySize);
        startService(dftIntent);
    }

    // metoda rozstrzygająca oś która ma być zapisywana
    private void putValueIntoAxisArray(String axis, int iteration){

        if(axis.equals("X"))
                accelerationValuesInWindow[iteration] = accelerationValueX;
        if(axis.equals("Y"))
                accelerationValuesInWindow[iteration] = accelerationValueY;
        if(axis.equals("Z"))
                accelerationValuesInWindow[iteration] = accelerationValueZ;

    }

    // metoda ustawiająca akcelerometr
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

    // metoda czytająca dane z akcelerometru
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

    // Nieużywana metoda inferfejsu SensorEventListener
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // metoda cyklu życia serwisu wywoływana podczas
    // jego zakończenia
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }
}
