package com.example.jakpe.vibrationdetector.interfaces;

import android.text.Editable;

/**
 * Created by pernal on 08.12.17.
 */

public interface MainContract {
    interface MainView{
        void startNewMeasurementActivity();
        void onInvalidInput(String numberInString);
        void onValidInput();
    }

    interface MainPresenter{
        void attach(MainContract.MainView view);
        void detach();
        void newMeasurementClicked();
        void validateText(Editable editable);
    }
}
