package com.example.jakpe.vibrationdetector.implementations;

import com.example.jakpe.vibrationdetector.interfaces.MeasurementRepository;

/**
 * Created by pernal on 09.12.17.
 */

public class MeasurementRepositoryImpl implements MeasurementRepository {

    public static MeasurementRepositoryImpl getInstance(){
        return  new MeasurementRepositoryImpl();
    }


    @Override
    public void loadData(int[] data) {

    }
}
