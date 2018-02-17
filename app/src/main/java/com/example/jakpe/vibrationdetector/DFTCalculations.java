package com.example.jakpe.vibrationdetector;

/**
 * Created by pernal on 10.12.17.
 */

public class DFTCalculations {

    private double outReal[];
    private double outImag[];
    private double absValue[];
    private double highestAccelerationAmplitude=0;
    private double highestDisplacementAmplitude=0;

    public double getHighestDisplacementAmplitude() {
        return roundDoubleValue(highestDisplacementAmplitude,2);
    }

    public double getHighestAmplitude() {
        return roundDoubleValue(highestAccelerationAmplitude, 2);
    }

    public void calculateDFT(double[] inreal) {
        int n = inreal.length;
        outReal = new double[n];
        outImag = new double[n];
        for (int k = 0; k < n; k++) {  // For each output element
            double sumReal = 0;
            double sumImag = 0;
            for (int t = 0; t < n; t++) {  // For each input element
                double angle = 2 * Math.PI * t * k / n;
                sumReal +=  inreal[t] * Math.cos(angle);
                sumImag += -inreal[t] * Math.sin(angle);
            }
            outReal[k] = sumReal/(0.4*n);
            outImag[k] = sumImag/(0.4*n);
        }
    }

    public double[] calculateAndGetAbsValueOfDFT(){
        absValue = new double[outReal.length];
        for(int i=0; i<absValue.length; i++){
            absValue[i] = Math.sqrt(Math.pow(outReal[i], 2) + Math.pow(outImag[i], 2));
        }

        return absValue;
    }

    public double calculateHighestFrequency(double frequencySize){
        double frequency = 0;

        for(int i=0; i<absValue.length/2; i++){

            if(absValue[i] > highestAccelerationAmplitude){
                highestAccelerationAmplitude=absValue[i];
                frequency=i*frequencySize;
            }

        }

        frequency = roundDoubleValue(frequency, 2);
        calculateHighestDisplacementAmplitude(frequency);
        return frequency;
    }

    private void calculateHighestDisplacementAmplitude(double frequency){
        if(frequency!=0)
            highestDisplacementAmplitude = highestAccelerationAmplitude/Math.pow(2*Math.PI*frequency,2)*1000;
        else
            highestDisplacementAmplitude = 0;
    }

    public double calculateHighestAccelerationInWindowTime(double[] accelerationValues){
        double highestAccelerationValue=0;
        for(int i=0; i<accelerationValues.length; i++){
            if(accelerationValues[i]>highestAccelerationValue)
                highestAccelerationValue=accelerationValues[i];
        }

        return roundDoubleValue(highestAccelerationValue, 2);
    }

    public double getNumberOfSamples(){
        return absValue.length;
    }


    private double roundDoubleValue(double value, int precise){
        long factor=0;
        long tmp =0;

        factor = (long) Math.pow(10, precise);
        value = value * factor;
        tmp = Math.round(value);
        value = (double) tmp / factor;

        return value;
    }

}
