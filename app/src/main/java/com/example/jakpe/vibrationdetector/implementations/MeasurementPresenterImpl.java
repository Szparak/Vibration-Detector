package com.example.jakpe.vibrationdetector.implementations;

import android.support.annotation.Nullable;

import com.example.jakpe.vibrationdetector.interfaces.MeasurementContract;
import com.example.jakpe.vibrationdetector.interfaces.MeasurementRepository;

/**
 * Created by pernal on 08.12.17.
 */

public class MeasurementPresenterImpl implements MeasurementContract.MeasurementPresenter {

    @Nullable
    MeasurementContract.MeasurementView view;
    MeasurementRepository repository;

    public MeasurementPresenterImpl(MeasurementRepository repository){
        this.repository = repository;
    }

    @Override
    public void attach(MeasurementContract.MeasurementView view) {
        this.view = view;
    }

    @Override
    public void detach() {

    }
}
