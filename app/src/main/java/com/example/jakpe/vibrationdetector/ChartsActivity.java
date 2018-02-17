package com.example.jakpe.vibrationdetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.jakpe.vibrationdetector.services.DataService;
import com.example.jakpe.vibrationdetector.settings.AcquisitionSettingsActivity;
import com.example.jakpe.vibrationdetector.settings.ChartsSettings;
import com.example.jakpe.vibrationdetector.settings.ChartsSettingsActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartsActivity extends AppCompatActivity {

    @BindView(R.id.charts_toolbar)
    Toolbar chartsToolbar;

    @BindView(R.id.X_graph)
    GraphView xGraph;

    @BindView(R.id.Y_graph)
    GraphView yGraph;

    @BindView(R.id.Z_graph)
    GraphView zGraph;

    @BindView(R.id.X_Analysis)
    Button xAnalysis;

    @BindView(R.id.Y_Analysis)
    Button yAnalysis;

    @BindView(R.id.Z_Analysis)
    Button zZnalysis;



    private LineGraphSeries<DataPoint> xAxisAccSeries, yAxisAccSeries, zAxisAccSeries;
    double samplingFrequency;
    double vectorTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        ButterKnife.bind(this);

        vectorTime=0;
        initUi();
    }

    private void initUi(){

        setSupportActionBar(chartsToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.charts));
            ab.setIcon(R.drawable.wave);
        }


        xAnalysis.setOnClickListener(view -> {
            startAnalysisActivity("X");
        });

        yAnalysis.setOnClickListener(view -> {
            startAnalysisActivity("Y");
        });

        zZnalysis.setOnClickListener(view -> {
            startAnalysisActivity("Z");
        });

        configureGraphs(xGraph);
        configureGraphs(yGraph);
        configureGraphs(zGraph);
        addSeries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.settings_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int clickedItemID = item.getItemId();
        switch(clickedItemID){
            case R.id.charts_settings_other_menu:
                Intent chartsSettingsIntent = new Intent(this, ChartsSettingsActivity.class);
                startActivity(chartsSettingsIntent);
                break;
            case R.id.acquisition_settings_other_menu:
                Intent acquisitionSettingsIntent = new Intent(this, AcquisitionSettingsActivity.class);
                startActivity(acquisitionSettingsIntent);
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    private void startAnalysisActivity(String axis){
        Intent analysisActivity = new Intent(this, NewMeasurement.class);
        analysisActivity.putExtra("axis", axis);
        startActivity(analysisActivity);
    }

    private void configureGraphs(GraphView graph){
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(2);
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph.getGridLabelRenderer().setVerticalAxisTitle("a[m/s^2]");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("t[s]");
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.rgb(0,128,255));
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.rgb(0,128,255));

    }

    private void addSeries(){
        xAxisAccSeries = new LineGraphSeries<>();
        xAxisAccSeries.setTitle("x");
        xAxisAccSeries.setColor(Color.BLUE);
        xGraph.addSeries(xAxisAccSeries);

        yAxisAccSeries = new LineGraphSeries<>();
        yAxisAccSeries.setTitle("y");
        yAxisAccSeries.setColor(Color.RED);
        yGraph.addSeries(yAxisAccSeries);

        zAxisAccSeries = new LineGraphSeries<>();
        zAxisAccSeries.setTitle("z");
        zAxisAccSeries.setColor(Color.GREEN);
        zGraph.addSeries(zAxisAccSeries);
    }


    private BroadcastReceiver valuesReceiver = new BroadcastReceiver() {
        DataPoint xDataPoint, yDataPoint, zDataPoint;


        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                double xResultValue = intent.getDoubleExtra("resultValueX",0);
                double yResultValue = intent.getDoubleExtra("resultValueY",0);
                double zResultValue = intent.getDoubleExtra("resultValueZ",0);
                xDataPoint = new DataPoint(vectorTime, xResultValue);
                yDataPoint = new DataPoint(vectorTime, yResultValue);
                zDataPoint = new DataPoint(vectorTime, zResultValue);

                appendData(xDataPoint, xAxisAccSeries);
                appendData(yDataPoint, yAxisAccSeries);
                appendData(zDataPoint, zAxisAccSeries);


                vectorTime+=(1/samplingFrequency);
            }
        }
    };

    public void appendData(DataPoint dataPoint, LineGraphSeries<DataPoint> graphSeries) {
        graphSeries.appendData(dataPoint, true, 150);
    }

    @Override
    protected void onResume() {
        super.onResume();
        samplingFrequency = ChartsSettings.getSampligValue();
        IntentFilter filter = new IntentFilter(DataService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(valuesReceiver, filter);
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(valuesReceiver);
    }

    private void startDataService(){
        Intent mySensorIntent = new Intent(this, DataService.class);
        mySensorIntent.putExtra("Analysis Mode", "OFF");
        startService(mySensorIntent);
    }

}
