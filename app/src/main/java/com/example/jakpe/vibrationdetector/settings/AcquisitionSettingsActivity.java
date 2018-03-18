/**
 Aktywność odpowiedzialna za dodawanie ustawień globalnych
 **/
package com.example.jakpe.vibrationdetector.settings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jakpe.vibrationdetector.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AcquisitionSettingsActivity extends AppCompatActivity {

    // Podpięcie widoków do zmiennych
    @BindView(R.id.acquisition_settings_filename)
    EditText fileNameEditText;
    @BindView(R.id.acquisition_settings_description)
    EditText descriptionEditText;
    @BindView(R.id.acquisition_settings_sampling)
    EditText samplingEditText;
    @BindView(R.id.acquisition_settings_measurement_time)
    EditText measurementTimeEditText;
    @BindView(R.id.acquisition_save_button)
    Button saveAcquisitionStateButton;

    //inicjalizacja pól klasy
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    // metoda cyklu życia aktywności uruchamiana podczas jej tworzenia
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquisition_settings);
        ButterKnife.bind(this);

        // pobranie obiektu do przechowywania ustawień
        settings = getSharedPreferences("AcquisitionSettings", 0);
        editor = settings.edit();

        // listener reagujący na zdarzenie naciśnięcia przycisku
        saveAcquisitionStateButton.setOnClickListener(v -> {

            if(!samplingEditText.getText().toString().equals(""))
                putSamplingValueIntoSharedPreferencesAndConfigFile();

            putDescriptionValueIntoSharedPreferencesAndConfigFile();

            if(!measurementTimeEditText.getText().toString().equals(""))
                putMeasurementTimeValueIntoSharedPreferencesAndConfigFile();
            if(!fileNameEditText.getText().toString().equals(""))
                putFileNameValueIntoSharedPreferencesAndConfigFile();

            editor.apply();

            // wiadomość o poprawnym zapisaniu danych
            Toast.makeText(this, "Your settings has been successfully saved", Toast.LENGTH_LONG).show();
        });

    }

    // metoda zerująca licznik plików
    private void clearFileCounter() {
        AcquisitionSettings.fileCounter=0;
        editor.putInt("fileCounter", AcquisitionSettings.fileCounter);
    }

    // metoda zapisująca do ustawień wartość z pola edycji
    private void putSamplingValueIntoSharedPreferencesAndConfigFile(){
        Editable fromEditText = samplingEditText.getText();
        int samplingValueInHz=Integer.valueOf(fromEditText.toString());

        editor.putInt("samplingValueInHz", samplingValueInHz);
        AcquisitionSettings.setSamplingFrequency(samplingValueInHz);

    }

    // metoda zapisująca do ustawień wartość z pola edycji
    private void putFileNameValueIntoSharedPreferencesAndConfigFile(){
        Editable fromEditText = fileNameEditText.getText();

        String fileNameValue=fromEditText.toString();

        editor.putString("fileNameValue", fileNameValue);
        AcquisitionSettings.setFileName(fileNameValue);
        clearFileCounter();
    }

    // metoda zapisująca do ustawień wartość z pola edycji
    private void putDescriptionValueIntoSharedPreferencesAndConfigFile(){
        String descriptionValue="No content";
        Editable fromEditText = descriptionEditText.getText();

        if(fromEditText.length()>0)
            descriptionValue=fromEditText.toString();

        AcquisitionSettings.setDescription(descriptionValue);
    }

    // metoda zapisująca do ustawień wartość z pola edycji
    private void putMeasurementTimeValueIntoSharedPreferencesAndConfigFile(){
        int measurementTimeValueInSeconds=10;
        Editable fromEditText = measurementTimeEditText.getText();

        if(fromEditText.length()>0)
            measurementTimeValueInSeconds=Integer.valueOf(fromEditText.toString());

        editor.putInt("measurementTimeValueInSeconds", measurementTimeValueInSeconds);
        AcquisitionSettings.setMeasurementTime(measurementTimeValueInSeconds);
    }
}

















