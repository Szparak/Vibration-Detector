package com.example.jakpe.vibrationdetector.settings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jakpe.vibrationdetector.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartsSettingsActivity extends AppCompatActivity {

    // Podpięcie widoków do zmiennych
    @BindView(R.id.chart_settings_gravity_force)
    CheckBox gravityForceCheckbox;
    @BindView(R.id.chart_settings_sampling)
    EditText samplingValue;
    @BindView(R.id.chart_settings_window_time)
    EditText windowTimeValue;
    @BindView(R.id.chart_settings_save_button)
    Button saveChartsSettingsButton;

    //inicjalizacja pól klasy
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    // metoda cyklu życia aktywności uruchamiana podczas jej tworzenia
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts_settings);
        ButterKnife.bind(this);

        // pobranie obiektu do przechowywania ustawień
        settings = getSharedPreferences("ChartsSettings", 0);
        editor = settings.edit();

        checkAndSetGravityState();

        // listener reagujący na zdarzenie naciśnięcia przycisku
        saveChartsSettingsButton.setOnClickListener(v -> {
            if(!samplingValue.getText().toString().equals(""))
                putSamplingValueIntoSharedPreferencesAndConfigFile();

            if(!windowTimeValue.getText().toString().equals(""))
                putWindowTimeValueIntoSharedPreferencesAndConfigFile();

            putGravityForceIntoSharedPreferencesAndConfigFile();
            editor.apply();

            // wiadomość o poprawnym zapisaniu danych
            Toast.makeText(this, "Your settings has been successfully saved", Toast.LENGTH_LONG).show();
        });

    }

    // metoda ustawiająca stan checkboxa
    private void checkAndSetGravityState(){
        if(settings.getBoolean("gravityForceCheckboxState", false))
            gravityForceCheckbox.setChecked(true);
    }

    // metoda zapisująca do ustawień wartość z pola edycji
    private void putSamplingValueIntoSharedPreferencesAndConfigFile(){
        Editable fromEditText = samplingValue.getText();

        int samplingValueInHz = Integer.valueOf(fromEditText.toString());

        editor.putInt("samplingValueInHz", samplingValueInHz);
        ChartsSettings.setSampligValue(samplingValueInHz);
    }

    // metoda zapisująca do ustawień wartość z pola edycji
    private void putWindowTimeValueIntoSharedPreferencesAndConfigFile(){
        Editable fromEditText = windowTimeValue.getText();

        int windowTimeValueInSeconds = Integer.valueOf(fromEditText.toString());

        editor.putInt("windowTimeValueInSeconds", windowTimeValueInSeconds);
        ChartsSettings.setWindowTimeValue(windowTimeValueInSeconds);
    }

    // metoda zapisująca do ustawień wartość z pola edycji
    private void putGravityForceIntoSharedPreferencesAndConfigFile(){
        boolean gravityForceCheckboxState = gravityForceCheckbox.isChecked();
        editor.putBoolean("gravityForceCheckboxState", gravityForceCheckboxState);
        ChartsSettings.setGravityForce(gravityForceCheckboxState);
    }



}
