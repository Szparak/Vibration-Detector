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

import com.example.jakpe.vibrationdetector.implementations.MeasurementPresenterImpl;
import com.example.jakpe.vibrationdetector.implementations.MeasurementRepositoryImpl;
import com.example.jakpe.vibrationdetector.interfaces.MeasurementContract;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewMeasurement extends AppCompatActivity implements MeasurementContract.MeasurementView{

    private MeasurementPresenterImpl measurementPresenter = new MeasurementPresenterImpl(MeasurementRepositoryImpl.getInstance());
    private LineGraphSeries<DataPoint> XAxisAccSeries, YAxisAccSeries, ZAxisAccSeries;
    private BarGraphSeries<DataPoint> dftSeries;
    Intent mySensorIntent;
    @BindView(R.id.new_measurement_toolbar) Toolbar myToolbar;
//    @BindView(R.id.main_frequency) TextView mainFrequencyTextView;
//    @BindView(R.id.progress_bar) ProgressBar savingProgressBar;
    @BindView(R.id.accelerometer_graph) GraphView accelerometerGraph;
    @BindView(R.id.dtf_graph) GraphView dftGraph;
    long czas1,czas2;
    private int windowTime;
    private double vectorTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        windowTime = data.getInt("Window Time");

        initUi();
        setPresenter();
    }

    private void setPresenter(){
        measurementPresenter.attach(this);
    }

    private void initUi(){
//        savingProgressBar.setVisibility(View.INVISIBLE);
        vectorTime=0;

        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.nowy_pomiar));
            ab.setIcon(R.drawable.wave);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        configureGraphs();
        addSeries();
        startDataService("X");

    }

    private void addSeries(){
        XAxisAccSeries = new LineGraphSeries<>();
        XAxisAccSeries.setTitle("X axis acceleration [m/s^2]");
        XAxisAccSeries.setColor(Color.BLUE);
        accelerometerGraph.addSeries(XAxisAccSeries);

        YAxisAccSeries = new LineGraphSeries<>();
        YAxisAccSeries.setTitle("Y axis acceleration [m/s^2]");
        YAxisAccSeries.setColor(Color.RED);
        accelerometerGraph.addSeries(YAxisAccSeries);

        ZAxisAccSeries = new LineGraphSeries<>();
        ZAxisAccSeries.setTitle("Z axis acceleration [m/s^2]");
        ZAxisAccSeries.setColor(Color.GREEN);
        accelerometerGraph.addSeries(ZAxisAccSeries);

    }

    private void configureGraphs(){
        accelerometerGraph.getViewport().setXAxisBoundsManual(true);
        accelerometerGraph.getViewport().setMinX(0);
        accelerometerGraph.getViewport().setMaxX(2);
        accelerometerGraph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        accelerometerGraph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        accelerometerGraph.getLegendRenderer().setVisible(true);
        accelerometerGraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        accelerometerGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        accelerometerGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        accelerometerGraph.getGridLabelRenderer().setGridColor(Color.WHITE);

        dftGraph.getViewport().setMinX(0);
        dftGraph.getViewport().setScalable(true);
        dftGraph.getViewport().setScalableY(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.new_measurement_menu,menu);
        menu.setGroupCheckable(R.id.xyz_axis_group, true, true);

        MenuItem xAxis = menu.findItem(R.id.x_axis);
        xAxis.setChecked(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.x_axis:
                if(!item.isChecked()){
                    item.setChecked(true);
                    DataService.isStopped = true;
                    try {
                        Thread.sleep(20);
                        vectorTime+=0.02;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startDataService("X");
                }
                break;
            case R.id.y_axis:
                if(!item.isChecked()){
                    item.setChecked(true);
                    DataService.isStopped = true;
                    try {
                        Thread.sleep(20);
                        vectorTime+=0.02;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startDataService("Y");
                }
                break;
            case R.id.z_axis:
                if(!item.isChecked()){
                    item.setChecked(true);
                    DataService.isStopped = true;
                    try {
                        Thread.sleep(20);
                        vectorTime+=0.02;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startDataService("Z");
                }
                break;
            case R.id.save:


                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void startDataService(String axis){
        mySensorIntent = new Intent(this, DataService.class);
        mySensorIntent.putExtra("axis" ,  axis);
        mySensorIntent.putExtra("Window Time" , windowTime);
        startService(mySensorIntent);
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        DataPoint dataPoint;

        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                double resultValue = intent.getDoubleExtra("resultValue",0);
//                double xTimeVector = intent.getDoubleExtra("xTimeVector" , 0 );
                String axis = intent.getStringExtra("axis");

                if(axis!=null){
                    dataPoint = new DataPoint(vectorTime, resultValue);
                    switch(axis){
                        case "X":
                            appendXData(dataPoint);
                            break;
                        case "Y":
                            appendYData(dataPoint);
                            break;
                        case "Z":
                            appendZData(dataPoint);
                            break;
                    }
                }
                vectorTime+=0.02;

            }
        }
    };

    private BroadcastReceiver dftReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                double absDftValues[] = intent.getDoubleArrayExtra("acceleration values");
                double samplingFrequency = intent.getIntExtra("sampling frequency", 0 );
                double frequencySize = samplingFrequency/absDftValues.length;
                DataPoint[] dataPoints = new DataPoint[absDftValues.length];
                System.out.println(frequencySize);
                new Thread(() -> {
                    double xAxisPoint=0;

                    for(int i=0; i<dataPoints.length; i++){
                        dataPoints[i] = new DataPoint(xAxisPoint, absDftValues[i]);
                        xAxisPoint+=frequencySize;
                    }

                    showDftData(dataPoints, frequencySize);
                }).start();

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(DataService.ACTION);
        IntentFilter dftFilter = new IntentFilter(DFTService.ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(dftReceiver, dftFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dftReceiver);
    }

    public void appendXData(DataPoint dataPoint) {
        XAxisAccSeries.appendData(dataPoint, true, 250);
    }


    public void appendYData(DataPoint dataPoint) {
        YAxisAccSeries.appendData(dataPoint, true, 250);
    }


    public void appendZData(DataPoint dataPoint) {
        ZAxisAccSeries.appendData(dataPoint, true, 250);
    }

    public void showDftData(DataPoint[] dataPoints, double frequencySize){
        dftGraph.getViewport().setMaxX(dataPoints.length*frequencySize);
        dftGraph.removeAllSeries();
        dftSeries = new BarGraphSeries<>(dataPoints);
        dftGraph.addSeries(dftSeries);
    }

    @Override
    protected void onDestroy() {
        measurementPresenter.detach();
        super.onDestroy();
    }

}
