package com.example.jakpe.vibrationdetector.settings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jakpe.vibrationdetector.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AcquisitionSettingsActivity extends AppCompatActivity {

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

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acquisition_settings);
        ButterKnife.bind(this);

        settings = getSharedPreferences("AcquisitionSettings", 0);
        editor = settings.edit();

        saveAcquisitionStateButton.setOnClickListener(v -> {

            putSamplingValueIntoSharedPreferencesAndConfigFile();
            putFileNameValueIntoSharedPreferencesAndConfigFile();
            putDescriptionValueIntoSharedPreferencesAndConfigFile();
            putMeasurementTimeValueIntoSharedPreferencesAndConfigFile();
            editor.apply();


            Toast.makeText(this, "Your settings has been successfully saved", Toast.LENGTH_LONG).show();
        });

    }

    private void putSamplingValueIntoSharedPreferencesAndConfigFile(){
        int samplingValueInHz=500;
        Editable fromEditText = samplingEditText.getText();

        if(fromEditText.length()>0)
            samplingValueInHz=Integer.valueOf(fromEditText.toString());

        editor.putInt("samplingValueInHz", samplingValueInHz);
        AcquisitionSettings.setSamplingFrequency(samplingValueInHz);

    }

    private void putFileNameValueIntoSharedPreferencesAndConfigFile(){
        String fileNameValue="measurement";
        Editable fromEditText = fileNameEditText.getText();

        if(fromEditText.length()>0)
            fileNameValue=fromEditText.toString();

        editor.putString("fileNameValue", fileNameValue);
        AcquisitionSettings.setFileName(fileNameValue);
    }

    private void putDescriptionValueIntoSharedPreferencesAndConfigFile(){
        String descriptionValue="No content";
        Editable fromEditText = descriptionEditText.getText();

        if(fromEditText.length()>0)
            descriptionValue=fromEditText.toString();

        AcquisitionSettings.setDescription(descriptionValue);
    }

    private void putMeasurementTimeValueIntoSharedPreferencesAndConfigFile(){
        int measurementTimeValueInSeconds=10;
        Editable fromEditText = measurementTimeEditText.getText();

        if(fromEditText.length()>0)
            measurementTimeValueInSeconds=Integer.valueOf(fromEditText.toString());

        editor.putInt("measurementTimeValueInSeconds", measurementTimeValueInSeconds);
        AcquisitionSettings.setMeasurementTime(measurementTimeValueInSeconds);
    }
}

















