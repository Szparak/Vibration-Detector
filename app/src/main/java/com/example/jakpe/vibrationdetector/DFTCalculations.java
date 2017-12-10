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
            double sumreal = 0;
            double sumimag = 0;
            for (int t = 0; t < n; t++) {  // For each input element
                double angle = 2 * Math.PI * t * k / n;
                sumreal +=  inreal[t] * Math.cos(angle);
                sumimag += -inreal[t] * Math.sin(angle);
            }
            outReal[k] = sumreal;
            outImag[k] = sumimag;
        }
    }

    public double[] calculateAbsValueOfDFT(){
        absValue = new double[outReal.length];
        for(int i=0; i<absValue.length; i++){
            absValue[i] = Math.sqrt(Math.pow(outReal[i], 2) + Math.pow(outImag[i], 2));
        }

        return absValue;
    }

}
