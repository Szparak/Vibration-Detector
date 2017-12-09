package com.example.jakpe.vibrationdetector.implementations;

import android.text.Editable;

import com.example.jakpe.vibrationdetector.interfaces.MainContract;

/**
 * Created by pernal on 08.12.17.
 */

public class MainPresenterImpl implements MainContract.MainPresenter {

    MainContract.MainView view;

    @Override
    public void attach(MainContract.MainView view) {
        this.view = view;
    }

    @Override
    public void detach() {
        view=null;
    }

    @Override
    public void newMeasurementClicked() {
        view.startNewMeasurementActivity();
    }

    @Override
    public void validateText(Editable editable) {
        int inputNumber;
        String inputNumberInString = editable.toString();

        if(!inputNumberInString.equals("")){
            inputNumber = Integer.valueOf(inputNumberInString);
            if(inputNumber<=10 && inputNumber!=0)
                view.onValidInput();
            else
                view.onInvalidInput(inputNumberInString);
        }else
            view.onInvalidInput("nothing");
    }
}
