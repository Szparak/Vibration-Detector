package com.example.jakpe.vibrationdetector;

/**
 * Created by pernal on 10.12.17.
 */

public class DFTCalculations {

    private double outReal[];
    private double outImag[];
    private double absValue[];


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
            outReal[k] = sumReal;
            outImag[k] = sumImag;
        }
    }

    public double[] calculateAndGetAbsValueOfDFT(){
        absValue = new double[outReal.length];
        for(int i=0; i<absValue.length; i++){
            absValue[i] = Math.sqrt(Math.pow(outReal[i], 2) + Math.pow(outImag[i], 2));
        }

        return absValue;
    }

    public double[] calculateTwoHighestFrequencies(double frequencySize){
        double frequencies[] = new double[] {0 , 0};
        double firstHighestValue=0;
        double secondHighestValue=0;

        for(int i=0; i<absValue.length/2; i++){

            if(absValue[i] > firstHighestValue){
                secondHighestValue=firstHighestValue;
                frequencies[1]=frequencies[0];

                firstHighestValue=absValue[i];
                frequencies[0]=i*frequencySize;
                continue;
            }

            if(absValue[i] > secondHighestValue){
                secondHighestValue = absValue[i];
                frequencies[1]=i*frequencySize;
            }

        }
        frequencies[0] = roundDoubleValue(frequencies[0], 2);
        frequencies[1] = roundDoubleValue(frequencies[1], 2);
        return frequencies;
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
