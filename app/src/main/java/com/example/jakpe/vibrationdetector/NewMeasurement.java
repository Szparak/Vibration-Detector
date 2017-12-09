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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jakpe.vibrationdetector.implementations.MeasurementPresenterImpl;
import com.example.jakpe.vibrationdetector.implementations.MeasurementRepositoryImpl;
import com.example.jakpe.vibrationdetector.interfaces.MeasurementContract;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewMeasurement extends AppCompatActivity implements MeasurementContract.MeasurementView{

    private MeasurementPresenterImpl measurementPresenter = new MeasurementPresenterImpl(MeasurementRepositoryImpl.getInstance());
    private LineGraphSeries<DataPoint> XAxisAccSeries, YAxisAccSeries, ZAxisAccSeries;
    Intent mySensorIntent;
    @BindView(R.id.new_measurement_toolbar) Toolbar myToolbar;
    @BindView(R.id.main_frequency) TextView mainFrequencyTextView;
    @BindView(R.id.progress_bar) ProgressBar savingProgressBar;
    @BindView(R.id.accelerometer_graph) GraphView accelerometerGraph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_measurement);
        ButterKnife.bind(this);

        initUi();
        setPresenter();
    }

    private void setPresenter(){
        measurementPresenter.attach(this);
    }

    private void initUi(){
        savingProgressBar.setVisibility(View.INVISIBLE);

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
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.new_measurement_menu,menu);
        menu.setGroupCheckable(R.id.xyz_axis_group, true, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.x_axis:
                // zabicie poprzedniego intent servisu
                // start intent servisu na osi x

                mySensorIntent = new Intent(this, DataService.class);
                startService(mySensorIntent);
                break;
            case R.id.y_axis:
                // zabicie poprzedniego intent servisu
                // start intent servisu na osi y

                break;
            case R.id.z_axis:
                // zabicie poprzedniego intent servisu
                // start intent servisu na osi z

                break;
            case R.id.save:


                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            if (resultCode == RESULT_OK) {
                double resultValue = intent.getDoubleExtra("resultValue",0);
                double xAxisTime = intent.getDoubleExtra("xAxisTime" , 0 );
                appendXData(new DataPoint(xAxisTime, resultValue));
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(DataService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
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

    @Override
    protected void onDestroy() {
        measurementPresenter.detach();
        super.onDestroy();
    }
}
