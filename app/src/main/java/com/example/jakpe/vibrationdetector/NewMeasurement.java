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
import android.widget.TextView;

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
    private LineGraphSeries<DataPoint> axisAccSeries;
    Intent mySensorIntent;
    @BindView(R.id.new_measurement_toolbar)
    Toolbar myToolbar;
    @BindView(R.id.accelerometer_graph)
    GraphView accelerometerGraph;
    @BindView(R.id.dtf_graph)
    GraphView dftGraph;
    @BindView(R.id.first_freqency)
    TextView firstFrequencyTextView;
    @BindView(R.id.second_frequency)
    TextView secondFrequencyTextView;
    private int windowTime=5;
    String axis;
    private double vectorTime;
    StringBuilder stringBuilder = new StringBuilder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        axis = data.getString("axis");

        stringBuilder.append("resultValue").append(axis);
        System.out.println(stringBuilder.toString());

        initUi();
        setPresenter();
    }



    private void setPresenter(){
        measurementPresenter.attach(this);
    }

    private void initUi(){
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
    }

    private void addSeries(){
        axisAccSeries = new LineGraphSeries<>();
        axisAccSeries.setColor(Color.CYAN);
        accelerometerGraph.addSeries(axisAccSeries);
    }

    private void configureGraphs(){
        accelerometerGraph.getViewport().setXAxisBoundsManual(true);
        accelerometerGraph.getViewport().setMinX(0);
        accelerometerGraph.getViewport().setMaxX(2);
        accelerometerGraph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        accelerometerGraph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
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

//        switch(item.getItemId()){
//            case R.id.x_axis:
//                if(!item.isChecked()){
//                    item.setChecked(true);
//                    DataService.isStopped = true;
//                    try {
//                        Thread.sleep(20);
//                        vectorTime+=0.02;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
////                    startDataService("X");
//                }
//                break;
//            case R.id.y_axis:
//                if(!item.isChecked()){
//                    item.setChecked(true);
//                    DataService.isStopped = true;
//                    try {
//                        Thread.sleep(20);
//                        vectorTime+=0.02;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
////                    startDataService("Y");
//                }
//                break;
//            case R.id.z_axis:
//                if(!item.isChecked()){
//                    item.setChecked(true);
//                    DataService.isStopped = true;
//                    try {
//                        Thread.sleep(20);
//                        vectorTime+=0.02;
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
////                    startDataService("Z");
//                }
//                break;
//            case R.id.save:
//
//
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }



    private void startDataService(){
        mySensorIntent = new Intent(this, DataService.class);
        mySensorIntent.putExtra("axis" ,  axis);
        mySensorIntent.putExtra("Window Time" , windowTime);
        mySensorIntent.putExtra("Analysis Mode", "ON");
        startService(mySensorIntent);
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        DataPoint dataPoint;


        @Override
        public void onReceive(Context context, Intent intent) {

            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                double resultValue = intent.getDoubleExtra(stringBuilder.toString(),0);

                    dataPoint = new DataPoint(vectorTime, resultValue);
                    appendData(dataPoint);

                }
                vectorTime+=0.02;

            }

    };

    private BroadcastReceiver dftReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                double absDftValues[] = intent.getDoubleArrayExtra("acceleration values");
                double frequencySize = intent.getDoubleExtra("frequency size", 0 );
                double twoFrequencies[] = intent.getDoubleArrayExtra("highest frequencies");
                DataPoint[] dataPoints = new DataPoint[absDftValues.length];
                showFrequencies(twoFrequencies);

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
        startDataService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataService.isStopped = true;
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dftReceiver);
    }

    public void appendData(DataPoint dataPoint) {
        axisAccSeries.appendData(dataPoint, true, 250);
    }



    public void showDftData(DataPoint[] dataPoints, double frequencySize){
        dftGraph.getViewport().setMaxX(dataPoints.length*frequencySize);
        dftGraph.removeAllSeries();

        BarGraphSeries<DataPoint> dftSeries = new BarGraphSeries<>(dataPoints);
        dftGraph.addSeries(dftSeries);

    }

    private void showFrequencies(double[] twoFrequencies){
        String textF1 = "f1 = " + String.valueOf(twoFrequencies[0]) + "Hz";
        String textF2 = "f2 = " + String.valueOf(twoFrequencies[1]) + "Hz";
        firstFrequencyTextView.setText(textF1);
        secondFrequencyTextView.setText(textF2);
    }




    @Override
    protected void onDestroy() {
        measurementPresenter.detach();
        super.onDestroy();
    }

}
