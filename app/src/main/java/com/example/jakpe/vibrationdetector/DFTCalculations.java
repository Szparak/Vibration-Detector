/**
 Klasa odpowiedzialna za obliczanie parametrów
 **/
package com.example.jakpe.vibrationdetector;

public class DFTCalculations {

    // deklaracja pól klasy
    private double outReal[];
    private double outImag[];
    private double absValue[];
    private double highestAccelerationAmplitude=0;
    private double highestDisplacementAmplitude=0;

    // metoda pobierająca amplitude przemieszczenia
    public double getHighestDisplacementAmplitude() {
        return roundDoubleValue(highestDisplacementAmplitude,2);
    }

    // metoda pobierająca amplitude przyspieszenia
    public double getHighestAmplitude() {
        return roundDoubleValue(highestAccelerationAmplitude, 2);
    }

    // metoda obliczająca części rzeczywiste
    // i urojone widma
    public void calculateDFT(double[] inreal) {
        int N = inreal.length;
        outReal = new double[N];
        outImag = new double[N];
        for (int k = 0; k < N; k++) {
            double sumReal = 0;
            double sumImag = 0;
            for (int n = 0; n < N; n++) {
                double angle = 2 * Math.PI * n * k / N;
                sumReal +=  inreal[n] * Math.cos(angle);
                sumImag += -inreal[n] * Math.sin(angle);
            }
            outReal[k] = sumReal/N;
            outImag[k] = sumImag/N;
        }
    }

    // metoda obliczająca widmo amplitudowe
    public double[] calculateAndGetAbsValueOfDFT(){
        absValue = new double[outReal.length/2];
        for(int i=0; i<absValue.length; i++){
            absValue[i] = Math.sqrt(Math.pow(outReal[i], 2) +
                    Math.pow(outImag[i], 2));
        }

        return absValue;
    }

    // metoda obliczająca dominującą częstotliwość w oknie czasowym
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
        return roundDoubleValue(frequency, 2);
    }

    // metoda obliczająca największa amplitude przemieszczenia
    private void calculateHighestDisplacementAmplitude(double frequency){
        if(frequency!=0)
            highestDisplacementAmplitude = highestAccelerationAmplitude/
                    Math.pow(2*Math.PI*frequency,2)*1000;
        else
            highestDisplacementAmplitude = 0;
    }

    // metoda obliczająca największą wartość przyspieszenia w oknie
    public double calculateHighestAccelerationInWindowTime(double[] accelerationValues){
        double highestAccelerationValue=0;
        for(int i=0; i<accelerationValues.length; i++){
            if(accelerationValues[i]>highestAccelerationValue)
                highestAccelerationValue=accelerationValues[i];
        }

        return roundDoubleValue(highestAccelerationValue, 2);
    }

    // metoda pobierająca liczbę próbek
    public double getNumberOfSamples(){
        return absValue.length;
    }


    // metoda zaokrąglająca wartości typu double
    // do podanych miejsc po przecinku
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
