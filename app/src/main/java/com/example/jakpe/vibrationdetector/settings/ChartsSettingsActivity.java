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
    private CheckBox gravityForceCheckbox;
    @BindView(R.id.chart_settings_sampling)
    private EditText samplingValue;
    @BindView(R.id.chart_settings_window_time)
    private EditText windowTimeValue;
    @BindView(R.id.chart_settings_save_button)
    private Button saveChartsSettingsButton;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private ChartsSettings chartsSettings = ChartsSettings.getChartsSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts_settings);
        ButterKnife.bind(this);

        settings = getSharedPreferences("ChartsSettings", 0);
        editor = settings.edit();

        checkAndSetGravityState();

        saveChartsSettingsButton.setOnClickListener(v -> {
            if(!samplingValue.getText().toString().equals(""))
                putSamplingValueIntoSharedPreferencesAndConfigFile();

            if(!windowTimeValue.getText().toString().equals(""))
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
        Editable fromEditText = samplingValue.getText();

        int samplingValueInHz = Integer.valueOf(fromEditText.toString());

        editor.putInt("samplingValueInHz", samplingValueInHz);
        chartsSettings.setSamplingValue(samplingValueInHz);
    }

    private void putWindowTimeValueIntoSharedPreferencesAndConfigFile(){
        Editable fromEditText = windowTimeValue.getText();

        int windowTimeValueInSeconds = Integer.valueOf(fromEditText.toString());

        editor.putInt("windowTimeValueInSeconds", windowTimeValueInSeconds);
        chartsSettings.setWindowTimeValue(windowTimeValueInSeconds);
    }

    private void putGravityForceIntoSharedPreferencesAndConfigFile(){
        boolean gravityForceCheckboxState = gravityForceCheckbox.isChecked();
        editor.putBoolean("gravityForceCheckboxState", gravityForceCheckboxState);
        chartsSettings.setGravityForce(gravityForceCheckboxState);
    }
}
