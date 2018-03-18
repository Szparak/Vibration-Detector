
/**
    Aktywność startowa aplikacji, odpowiedzialna za wyświetlanie
    informacji o nazwie i wersji aplikacji
**/
package com.example.jakpe.vibrationdetector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.Window;
import android.widget.Button;


import com.example.jakpe.vibrationdetector.settings.AcquisitionSettings;
import com.example.jakpe.vibrationdetector.settings.AcquisitionSettingsActivity;
import com.example.jakpe.vibrationdetector.settings.ChartsSettings;
import com.example.jakpe.vibrationdetector.settings.ChartsSettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    // Podpięcie widoków do zmiennych
    @BindView(R.id.my_toolbar)
    Toolbar mainToolbar ;

    @BindView(R.id.show_charts_button)
    Button showChartsButton;


    // metoda cyklu życia aktywności uruchamiana podczas jej tworzenia
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        initUi();
        getSettingsFromSharedPreferencesAndSetChartsSettings();
        getSettingsFromSharedPreferencesAndSetAcquisitionSettings();
    }

    // metoda androidowa przypisująca menu do aktywności
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.settings_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // metoda androidowa reagująca na wciśnięcie widoku w menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int clickedItemID = item.getItemId();
        switch(clickedItemID){
            case R.id.charts_settings_other_menu:
                Intent chartsSettingsIntent = new Intent(this,
                        ChartsSettingsActivity.class);
                startActivity(chartsSettingsIntent);
                break;
            case R.id.acquisition_settings_other_menu:
                Intent acquisitionSettingsIntent = new Intent(this,
                        AcquisitionSettingsActivity.class);
                startActivity(acquisitionSettingsIntent);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    // metoda przypisująca ustawieniom ChartsSettings ich wartości
    // w przypadku braku ustawień przypisane zostaną domyślne
    private void getSettingsFromSharedPreferencesAndSetAcquisitionSettings() {

        SharedPreferences settings = getSharedPreferences("AcquisitionSettings",
                0);
        AcquisitionSettings.setMeasurementTime(settings.
                getInt("measurementTimeValueInSeconds", 10));
        AcquisitionSettings.setSamplingFrequency(settings.
                getInt("samplingValueInHz", 100));
        AcquisitionSettings.setDescription("***No content***");
        AcquisitionSettings.setFileName(settings.
                getString("fileNameValue", "measurement"));
        AcquisitionSettings.setFileCounter(settings.
                getInt("fileCounter", 0));

    }

    // metoda przypisująca ustawieniom AcquisitionSettings ich wartości
    // w przypadku braku ustawień przypisane zostaną domyślne
    private void getSettingsFromSharedPreferencesAndSetChartsSettings(){

        SharedPreferences settings = getSharedPreferences("ChartsSettings",
                0);
        ChartsSettings.setGravityForce(settings.
                getBoolean("gravityForceCheckboxState", false));
        ChartsSettings.setWindowTimeValue(settings.
                getInt("windowTimeValueInSeconds", 5));
        ChartsSettings.setSampligValue(settings.
                getInt("samplingValueInHz", 50));
    }

    //metoda inicjująca ustawienia interfejs
    private void initUi(){

        setSupportActionBar(mainToolbar);
        ActionBar mainActionBar = getSupportActionBar();

        if(mainActionBar!=null){
            mainActionBar.setTitle(getString(R.string.main_title));
            mainActionBar.setIcon(R.drawable.wave);
        }

        // listener nasłuchujący zdarzenia wciśnięcia przycisku
        showChartsButton.setOnClickListener(v -> startNewMeasurementActivity());

    }


    // metoda uruchamiająca aktywność ChartsActivity
    public void startNewMeasurementActivity() {
        final Intent chartsActivity = new Intent(this, ChartsActivity.class);
        startActivity(chartsActivity);
    }



}
