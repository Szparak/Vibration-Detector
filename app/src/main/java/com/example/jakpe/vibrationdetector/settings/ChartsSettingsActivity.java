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

    @BindView(R.id.chart_settings_gravity_force)
    CheckBox gravityForceCheckbox;
    @BindView(R.id.chart_settings_sampling)
    EditText samplingValue;
    @BindView(R.id.chart_settings_window_time)
    EditText windowTimeValue;
    @BindView(R.id.chart_settings_save_button)
    Button saveChartsSettingsButton;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts_settings);
        ButterKnife.bind(this);

        settings = getSharedPreferences("ChartsSettings", 0);
        editor = settings.edit();

        checkAndSetGravityState();

        saveChartsSettingsButton.setOnClickListener(v -> {
                putSamplingValueIntoSharedPreferencesAndConfigFile();
                putWindowTimeValueIntoSharedPreferencesAndConfigFile();
                putGravityForceIntoSharedPreferencesAndConfigFile();
                editor.apply();

                Toast.makeText(this, "Your settings has been successfully saved", Toast.LENGTH_LONG).show();
        });

    }

    private void checkAndSetGravityState(){
        if(settings.getBoolean("gravityForceCheckboxState", false))
            gravityForceCheckbox.setChecked(true);
    }

    private void putSamplingValueIntoSharedPreferencesAndConfigFile(){
        int samplingValueInHz=50;
        Editable fromEditText = samplingValue.getText();

        if(fromEditText.length()>0)
            samplingValueInHz = Integer.valueOf(fromEditText.toString());


        editor.putInt("samplingValueInHz", samplingValueInHz);
        ChartsSettings.setSampligValue(samplingValueInHz);
    }

    private void putWindowTimeValueIntoSharedPreferencesAndConfigFile(){
        int windowTimeValueInSeconds = 5;
        Editable fromEditText = windowTimeValue.getText();

        if(fromEditText.length()>0)
            windowTimeValueInSeconds = Integer.valueOf(fromEditText.toString());


        editor.putInt("windowTimeValueInSeconds", windowTimeValueInSeconds);
        ChartsSettings.setWindowTimeValue(windowTimeValueInSeconds);
    }

    private void putGravityForceIntoSharedPreferencesAndConfigFile(){
        boolean gravityForceCheckboxState = gravityForceCheckbox.isChecked();
        editor.putBoolean("gravityForceCheckboxState", gravityForceCheckboxState);
        ChartsSettings.setGravityForce(gravityForceCheckboxState);
    }



}
