package com.example.jakpe.vibrationdetector.interfaces;

/**
 * Created by pernal on 08.12.17.
 */

public interface MeasurementContract {
    interface MeasurementView{

    }

    interface MeasurementPresenter{
        void attach(MeasurementView view);
        void detach();
    }
}
