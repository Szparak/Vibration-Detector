package com.example.jakpe.vibrationdetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jakpe.vibrationdetector.services.DFTService;
import com.example.jakpe.vibrationdetector.services.DataService;
import com.example.jakpe.vibrationdetector.settings.AcquisitionSettings;
import com.example.jakpe.vibrationdetector.settings.AcquisitionSettingsActivity;
import com.example.jakpe.vibrationdetector.settings.ChartsSettings;
import com.example.jakpe.vibrationdetector.settings.ChartsSettingsActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.val;

public class AnalysisActivity extends AppCompatActivity {

    @BindView(R.id.new_measurement_toolbar)
    Toolbar myToolbar;
    @BindView(R.id.accelerometer_graph)
    GraphView accelerometerGraph;
    @BindView(R.id.dtf_graph)
    GraphView dftGraph;
    @BindView(R.id.first_freqency)
    TextView firstFrequencyTextView;
    @BindView(R.id.amplitude_for_frequency)
    TextView maxFrequencyAmplitudeTextView;
    @BindView(R.id.displacement_amplitude_for_frequency)
    TextView displacementAmplitudeForMaxFrequencyAmplitudeTextView;
    @BindView(R.id.max_acceleration_in_window_time)
    TextView maxAccelerationInWindowTimeTextView;
    @BindView(R.id.amount_of_samples)
    TextView amountOfSamplesTextView;
    @BindView(R.id.date_textView)
    TextView dateTextView;
    @BindView(R.id.acquisition_progress_bar)
    ProgressBar acquisitionProgressBar;

    private String axis;
    private double vectorTime;
    private StringBuilder stringBuilder = new StringBuilder();
    private double samplingFrequency;
    private LineGraphSeries<DataPoint> axisAccSeries;
    private AcquisitionSettings acquisitionSettings = AcquisitionSettings.getAcquisitionSettings();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        ButterKnife.bind(this);

        vectorTime = 0.02;
        Bundle data = getIntent().getExtras();
        axis = data.getString("axis");
        FileSaver.verifyStoragePermissions(this);

        stringBuilder.append("resultValue").append(axis);

        initUi();
    }

    private void initUi() {
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.analysis_charts));
            ab.setIcon(R.drawable.wave);
        }

        acquisitionProgressBar.setVisibility(View.INVISIBLE);
        configureGraphs();
        addSeries();
    }

    private void addSeries() {
        axisAccSeries = new LineGraphSeries<>();
        axisAccSeries.setColor(Color.CYAN);
        accelerometerGraph.addSeries(axisAccSeries);
    }

    private void configureGraphs() {
        accelerometerGraph.getViewport().setXAxisBoundsManual(true);
        accelerometerGraph.getViewport().setMinX(0);
        accelerometerGraph.getViewport().setMaxX(2);
        accelerometerGraph.getViewport().setScalable(true);
        accelerometerGraph.getViewport().setScalableY(true);
        accelerometerGraph.getGridLabelRenderer()
                .setHorizontalLabelsColor(Color.WHITE);
        accelerometerGraph.getGridLabelRenderer()
                .setVerticalLabelsColor(Color.WHITE);
        accelerometerGraph.getGridLabelRenderer()
                .setGridColor(Color.WHITE);
        accelerometerGraph.getGridLabelRenderer()
                .setHorizontalAxisTitleColor(Color.rgb(0, 128, 255));
        accelerometerGraph.getGridLabelRenderer()
                .setVerticalAxisTitleColor(Color.rgb(0, 128, 255));
        accelerometerGraph.getGridLabelRenderer()
                .setHorizontalAxisTitle("t[s]");
        accelerometerGraph.getGridLabelRenderer()
                .setVerticalAxisTitle("a[m/s^2]");

        dftGraph.getViewport().setMinX(0);
        dftGraph.getViewport().setScalable(true);
        dftGraph.getViewport().setScalableY(true);
        dftGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        dftGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        dftGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
        dftGraph.getGridLabelRenderer().setHorizontalAxisTitle("f[Hz]");
        dftGraph.getGridLabelRenderer().setVerticalAxisTitle("A[m/s^2]");
        dftGraph.getGridLabelRenderer()
                .setHorizontalAxisTitleColor(Color.rgb(0, 128, 255));
        dftGraph.getGridLabelRenderer()
                .setVerticalAxisTitleColor(Color.rgb(0, 128, 255));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.analysis_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences settings = getSharedPreferences("AcquisitionSettings", 0);
        SharedPreferences.Editor editor = settings.edit();

        int clickedItemID = item.getItemId();
        switch (clickedItemID) {
            case R.id.charts_settings:
                Intent chartsSettingsIntent = new Intent(this,
                        ChartsSettingsActivity.class);
                startActivity(chartsSettingsIntent);
                break;
            case R.id.acquisition_settings:
                Intent acquisitionSettingsIntent = new Intent(this,
                        AcquisitionSettingsActivity.class);
                startActivity(acquisitionSettingsIntent);
                break;
            case R.id.save:
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                acquisitionProgressBar.setMax(acquisitionSettings.
                        getMeasurementTime() * acquisitionSettings.getSamplingFrequency());
                acquisitionProgressBar.setVisibility(View.VISIBLE);
                DataService.writingMode = true;
                DataService.isStopped = true;
                FileSaver.measurementStartDate = Calendar.getInstance().getTime();
                acquisitionSettings.incrementFileCounter();
                editor.putInt("fileCounter", acquisitionSettings.getFileCounter());
                editor.apply();
                updateProgressbar();
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateProgressbar() {
        val samplingFrequency = acquisitionSettings.getSamplingFrequency();
        val measurementTime = acquisitionSettings.getMeasurementTime();

        new Thread(() -> {
            acquisitionProgressBar.setProgress(0);
            for (int i = 0; i < samplingFrequency * measurementTime; i++) {

                acquisitionProgressBar.incrementProgressBy(1);
                try {
                    Thread.sleep(1000 / acquisitionSettings.getSamplingFrequency());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startDataService();
        }).start();
    }

    private void startDataService() {
        Intent mySensorIntent = new Intent(this, DataService.class);
        mySensorIntent.putExtra("axis", axis);
        mySensorIntent.putExtra("Analysis Mode", "ON");
        startService(mySensorIntent);
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        DataPoint dataPoint;
        Date previousDate;

        @Override
        public void onReceive(Context context, Intent intent) {
            Date date = Calendar.getInstance().getTime();

            if (!date.equals(previousDate)) {
                dateTextView.setText(date.toString());
                previousDate = date;
            }

            int resultCode = intent.getIntExtra("resultCode",
                    RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                double resultValue = intent.getDoubleExtra(stringBuilder.
                        toString(), 0);

                dataPoint = new DataPoint(vectorTime, resultValue);
                appendData(dataPoint);

            }
            vectorTime += (1 / samplingFrequency);
        }

    };

    private BroadcastReceiver dftReceiver = new BroadcastReceiver() {

        // metoda wykonywana przy odbiorze transmisji
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode",
                    RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                double absDftValues[] = intent
                        .getDoubleArrayExtra("acceleration values");
                double frequencySize = intent
                        .getDoubleExtra("frequency size", 0);
                double frequency = intent
                        .getDoubleExtra("highest frequency", 0);
                double amplitudeForMaxFrequency = intent
                        .getDoubleExtra("amplitudeForMaxFrequency", 0);
                double displacementAmplitudeForMaxFrequency = intent
                        .getDoubleExtra("displacementAmplitudeForMaxFrequency", 0);
                double maxAccelerationInWindowTime = intent
                        .getDoubleExtra("maxAccelerationInWindowTime", 0);
                double numberOfSamples = intent
                        .getDoubleExtra("numberOfSamples", 0);
                DataPoint[] dataPoints = new DataPoint[absDftValues.length];
                setFrequency(frequency);
                setMaxFrequencyAmplitude(amplitudeForMaxFrequency);
                setDisplacementAmplitudeForMaxFrequency(displacementAmplitudeForMaxFrequency);
                setMaxAccelerationInWindowTimeTextView(maxAccelerationInWindowTime);
                setAmountOfSamplesTextView(numberOfSamples);

                // start nowego wątku wyświetlającego dane DFT
                new Thread(() -> {
                    double xAxisPoint = 0;
                    for (int i = 0; i < dataPoints.length; i++) {
                        dataPoints[i] = new DataPoint(xAxisPoint, absDftValues[i]);
                        xAxisPoint += frequencySize;
                    }
                    showDftData(dataPoints, frequencySize);
                }).start();

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        samplingFrequency = ChartsSettings.getChartsSettings().getSamplingValue();

        IntentFilter filter = new IntentFilter(DataService.ACTION);
        IntentFilter dftFilter = new IntentFilter(DFTService.ACTION);

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(testReceiver, filter);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(dftReceiver, dftFilter);
        startDataService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataService.isStopped = true;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dftReceiver);
    }

    public void appendData(DataPoint dataPoint) {
        axisAccSeries.appendData(dataPoint, true, 250);
    }

    public void showDftData(DataPoint[] dataPoints, double frequencySize) {
        dftGraph.getViewport().setMaxX(10);
        dftGraph.removeAllSeries();

        BarGraphSeries<DataPoint> dftSeries = new BarGraphSeries<>(dataPoints);
        dftSeries.setColor(Color.RED);
        dftGraph.addSeries(dftSeries);

    }
    private void setFrequency(double frequency) {
        String textF1 = "f1 = " + String.valueOf(frequency) + "Hz";
        firstFrequencyTextView.setText(textF1);
    }

    public void setMaxFrequencyAmplitude(double maxFrequencyAmplitude) {
        String textAmax = "Amax = " + String.valueOf(maxFrequencyAmplitude) + "m/s^2";
        maxFrequencyAmplitudeTextView.setText(textAmax);
    }

    public void setDisplacementAmplitudeForMaxFrequency(double displacementAmplitudeForMaxFrequency) {
        String textXmax = "Xmax = " + String.valueOf(displacementAmplitudeForMaxFrequency) + "mm";
        displacementAmplitudeForMaxFrequencyAmplitudeTextView.setText(textXmax);
    }

    public void setMaxAccelerationInWindowTimeTextView(double maxAccelerationInWindowTime) {
        String textAccelerationMax = "a_max = " + String.valueOf(maxAccelerationInWindowTime) + "m/s^2";
        maxAccelerationInWindowTimeTextView.setText(textAccelerationMax);
    }

    public void setAmountOfSamplesTextView(double amountOfSamples) {
        String textAmountOfSamples = "N = " + String.valueOf(amountOfSamples);
        amountOfSamplesTextView.setText(textAmountOfSamples);
    }
}
