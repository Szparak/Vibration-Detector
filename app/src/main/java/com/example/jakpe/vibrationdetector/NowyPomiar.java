package com.example.jakpe.vibrationdetector;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jakpe.vibrationdetector.interfaces.MeasurementContract;
import com.jaredrummler.android.device.DeviceName;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.PI;

public class NowyPomiar extends AppCompatActivity implements SensorEventListener, MeasurementContract.MeasurementView {

    Toolbar myToolbar;
    CheckBox checkBoxX,checkBoxY,checkBoxZ;
    TextView czestX,czestY,czestZ;
    EditText editTime, editSample, editName, descEdit;
    Button start;
    ProgressBar myBar;
    Date startTime,endTime;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Toast toast;
    private double warX=0,warY=0,warZ=0;
    private LineGraphSeries<DataPoint> mSeriesZ,mSeriesX,mSeriesY,mAmplitudeX,mAmplitudeY,mAmplitudeZ;
    private Runnable mTimer2,mTimer1;
    private long czas;
    private final Handler mHandler = new Handler();
    double x=0,gravityX,gravityY,gravityZ;
    double okresX=0,czasPoczX=0,czestotliwoscX=0,okresY=0,okresZ=0,czestotliwoscY=0,czestotliwoscZ=0,czasPoczY=0,czasPoczZ=0;
    boolean flagaWiekszyY=false,flagaWiekszyX=false,flagaWiekszyZ=false;
    long factor,tmp;
    double omega;
    int czest_pomiaru=50;
    double probkowanie=0;
    double wektor_czasu=0;
    int is_measurement_flag=0;
    String nazwa,opis;
    int progress=0;
    int rozmiarTablicy=0,czas_pomiaru;
    double amplitudaX[],amplitudaY[],amplitudaZ[],czasPomiaru[];
    double wektor_czasu_pomiaru=0;
    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowy_pomiar);
        myToolbar = (Toolbar) findViewById(R.id.nowy_pomiar_toolbar);
        checkBoxX = (CheckBox) findViewById(R.id.checkBoxX);
        checkBoxY = (CheckBox) findViewById(R.id.checkBoxY);
        checkBoxZ = (CheckBox) findViewById(R.id.checkBoxZ);
        czestX = (TextView) findViewById(R.id.czestotliwoscX);
        czestY = (TextView) findViewById(R.id.czestotliwoscY);
        czestZ = (TextView) findViewById(R.id.czestotliwoscZ);
        myBar = (ProgressBar) findViewById(R.id.progressBar);
        myBar.setVisibility(View.INVISIBLE);
        czest_pomiaru = 50;
        is_measurement_flag=0;
        progress=0;
        df.setMaximumFractionDigits(340);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.nowy_pomiar));
        getSupportActionBar().setIcon(R.drawable.wave);
        ActionBar ab = getSupportActionBar();
        czas = SystemClock.uptimeMillis();
        toast = Toast.makeText(this,"Nie posiadasz akcelerometru w swoim telefonie", Toast.LENGTH_LONG);
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = null;

        if (mSensor == null){
            // Use the accelerometer.
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
            else{
                toast.show();
            }
        }
        GraphView graph = (GraphView) findViewById(R.id.graph);
        GraphView graph1 = (GraphView) findViewById(R.id.graph1);

        mAmplitudeX = new LineGraphSeries<>();
        mAmplitudeX.setTitle("X axis amplitude[mm]");
        mAmplitudeX.setColor(Color.BLUE);
        graph1.addSeries(mAmplitudeX);

        mAmplitudeY = new LineGraphSeries<>();
        mAmplitudeY.setTitle("Y axis amplitude[mm]");
        mAmplitudeY.setColor(Color.RED);
        graph1.addSeries(mAmplitudeY);

        mAmplitudeZ = new LineGraphSeries<>();
        mAmplitudeZ.setTitle("Z axis amplitude[mm]");
        mAmplitudeZ.setColor(Color.GREEN);
        graph1.addSeries(mAmplitudeZ);

        mSeriesX = new LineGraphSeries<>();
        mSeriesX.setTitle("X axis acceleration [m/s^2]");
        mSeriesX.setColor(Color.BLUE);
        graph.addSeries(mSeriesX);

        mSeriesY = new LineGraphSeries<>();
        mSeriesY.setTitle("Y axis acceleration [m/s^2]");
        mSeriesY.setColor(Color.RED);
        graph.addSeries(mSeriesY);

        mSeriesZ = new LineGraphSeries<>();
        mSeriesZ.setTitle("Z axis acceleration [m/s^2]");
        mSeriesZ.setColor(Color.GREEN);
        graph.addSeries(mSeriesZ);

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

        graph1.getViewport().setXAxisBoundsManual(true);
        graph1.getViewport().setMinX(0);
        graph1.getViewport().setMaxX(2);
        graph1.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph1.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        graph1.getLegendRenderer().setVisible(true);
        graph1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph1.getGridLabelRenderer().setGridColor(Color.WHITE);
        graph1.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph1.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);

}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.new_measurement_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if(itemId == R.id.save){

            final AlertDialog.Builder mbuilder = new AlertDialog.Builder(NowyPomiar.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_data,null);
            editTime = (EditText) mView.findViewById(R.id.measurement_time);
            editSample = (EditText) mView.findViewById(R.id.sammpling_time);
            start = (Button) mView.findViewById(R.id.start_button);
            editName = (EditText) mView.findViewById(R.id.fileName);
            descEdit = (EditText) mView.findViewById(R.id.descriptionEdit);
            is_measurement_flag=0;
            mbuilder.setView(mView);
            final AlertDialog dialog = mbuilder.create();

            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!editSample.getText().toString().isEmpty() && !editTime.getText().toString().isEmpty()){
                        czest_pomiaru = Integer.parseInt(editSample.getText().toString());
                        czas_pomiaru = Integer.parseInt(editTime.getText().toString());
                        nazwa = editName.getText().toString();
                        opis = descEdit.getText().toString();
                        probkowanie = (double) 1/czest_pomiaru*1000;
                        wektor_czasu = (double) 1/czest_pomiaru;
                        rozmiarTablicy = czest_pomiaru*czas_pomiaru;
                        amplitudaX = new double[rozmiarTablicy];
                        amplitudaY = new double[rozmiarTablicy];
                        amplitudaZ = new double[rozmiarTablicy];
                        czasPomiaru = new double[rozmiarTablicy];
                        is_measurement_flag=1;
                        progress=0;
                        startTime=Calendar.getInstance().getTime();
                        wektor_czasu_pomiaru=0;
                        myBar.setMax(rozmiarTablicy);
                        myBar.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        final double alpha = 0.8;

        // Isolate the force of gravity with the low-pass filter.
        gravityX = alpha * gravityX + (1 - alpha) * event.values[0];
        gravityY = alpha * gravityY + (1 - alpha) * event.values[1];
        gravityZ = alpha * gravityZ + (1 - alpha) * event.values[2];


        warX=event.values[0] - gravityX;
        warY=event.values[1] - gravityY;
        warZ=event.values[2] - gravityZ;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        checkBoxX.setChecked(true);
        checkBoxY.setChecked(false);
        checkBoxZ.setChecked(false);
        czasPoczX = 0;
        czasPoczY = 0;
        czasPoczZ = 0;
        flagaWiekszyX = false;
        flagaWiekszyY = false;
        flagaWiekszyZ = false;
        probkowanie = (double) 1/czest_pomiaru*1000;
        wektor_czasu = (double) 1/czest_pomiaru;



        mTimer2 = new Runnable() {
            @Override
            public void run() {
                czas += probkowanie;
                mHandler.postAtTime(this, czas);


                if (checkBoxX.isChecked()) {
                    mSeriesX.appendData(new DataPoint(x, warX), true, 250);

                }
                if (checkBoxY.isChecked()) {
                    mSeriesY.appendData(new DataPoint(x, warY), true, 250);

                }
                if (checkBoxZ.isChecked()) {
                    mSeriesZ.appendData(new DataPoint(x, warZ), true, 250);

                }

                x += wektor_czasu;
            }
        };
        mHandler.postDelayed(mTimer2, 100);

        mTimer1 = new Runnable() {
            @Override
            public void run() {
                mHandler.postAtTime(this, czas);

                if(is_measurement_flag==1)
                    wektor_czasu_pomiaru+=wektor_czasu;

                if (warX >= 0 && flagaWiekszyX == false) {
                    if (czasPoczX != 0) {
                        okresX = (czas - czasPoczX) / 1000;
                        czestotliwoscX = 1 / okresX;

                        if (checkBoxX.isChecked()) {
                            czestX.setVisibility(View.VISIBLE);
                            czestX.setText("fx=" + zaokraglij(czestotliwoscX,2) + "Hz");
                        } else {
                            czestX.setVisibility(View.INVISIBLE);
                        }
                    }
                    czasPoczX = czas;
                    flagaWiekszyX = true;
                }
                if (warX < 0) {
                    flagaWiekszyX = false;
                }

                if (warY >= 0 && flagaWiekszyY == false) {
                    if (czasPoczY != 0) {
                        okresY = (czas - czasPoczY) / 1000;
                        czestotliwoscY = 1 / okresY;
                        if (checkBoxY.isChecked()) {
                            czestY.setVisibility(View.VISIBLE);
                            czestY.setText("fy=" + zaokraglij(czestotliwoscY,2) + "Hz");
                        } else {
                            czestY.setVisibility(View.INVISIBLE);
                        }
                    }
                    czasPoczY = czas;
                    flagaWiekszyY = true;
                }
                if (warY < 0) {
                    flagaWiekszyY = false;
                }

                if (warZ >= 0 && flagaWiekszyZ == false) {
                    if (czasPoczZ != 0) {
                        okresZ = (czas - czasPoczZ) / 1000;
                        czestotliwoscZ = 1 / okresZ;
                        if (checkBoxZ.isChecked()) {
                            czestZ.setVisibility(View.VISIBLE);
                            czestZ.setText("fz=" + zaokraglij(czestotliwoscZ,2) + "Hz");
                        } else {
                            czestZ.setVisibility(View.INVISIBLE);
                        }
                    }
                    czasPoczZ = czas;
                    flagaWiekszyZ = true;
                }
                if (warZ < 0) {
                    flagaWiekszyZ = false;
                }

                if (checkBoxX.isChecked()) {
                    mAmplitudeX.appendData(new DataPoint(x, obliczPrzemieszczenie(okresX,warX)), true, 250);
                    if(is_measurement_flag==1 && progress<rozmiarTablicy){
                        amplitudaX[progress]=obliczPrzemieszczenie(okresX,warX);
                    }
                }
                if (checkBoxY.isChecked()) {
                    mAmplitudeY.appendData(new DataPoint(x,obliczPrzemieszczenie(okresY,warY)), true, 250);
                    if(is_measurement_flag==1 && progress<rozmiarTablicy){
                        amplitudaY[progress]=obliczPrzemieszczenie(okresY,warY);
                    }
                }
                if (checkBoxZ.isChecked()) {
                    mAmplitudeZ.appendData(new DataPoint(x, obliczPrzemieszczenie(okresZ,warZ)), true, 250);
                    if(is_measurement_flag==1 && progress<rozmiarTablicy){
                        amplitudaZ[progress]=obliczPrzemieszczenie(okresZ,warZ);
                    }
                }

                if(is_measurement_flag==1){
                    czasPomiaru[progress]=zaokraglij(wektor_czasu_pomiaru,2);
                    myBar.incrementProgressBy(1);
                    progress++;
                }

                if(progress==rozmiarTablicy && is_measurement_flag==1){
                    if(checkBoxX.isChecked()){
                        try {
                            utworzPlik("X",amplitudaX,czasPomiaru);
                        } catch (IOException e) {
                            System.out.println("Tworzenie pliku nie powiodło sie");
                            e.printStackTrace();
                        }
                    }
                    if(checkBoxY.isChecked()){
                        try {
                            utworzPlik("Y",amplitudaY,czasPomiaru);
                        } catch (IOException e) {
                            System.out.println("Tworzenie pliku nie powiodło sie");
                        }
                    }
                    if(checkBoxZ.isChecked()){
                        try {
                            utworzPlik("Z",amplitudaZ,czasPomiaru);
                        } catch (IOException e) {
                            System.out.println("Tworzenie pliku nie powiodło sie");
                        }
                    }


                    is_measurement_flag=0;
                    progress=0;
                    wektor_czasu=0.02;
                    probkowanie=20;
                    myBar.setVisibility(View.INVISIBLE);
                }




            }

        };
        mHandler.postDelayed(mTimer1, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mHandler.removeCallbacks(mTimer2);
        mHandler.removeCallbacks(mTimer1);

    }


    public double obliczPrzemieszczenie(double okres, double przyspieszenie){
        double amplituda=0;

        omega = (2*PI)/okres;
        amplituda = - (przyspieszenie/(omega*omega));

        return amplituda*1000;
    }

    public double zaokraglij(double czestotliwosc, int dokladnosc){

        factor = (long) Math.pow(10, dokladnosc);
        czestotliwosc = czestotliwosc * factor;
        tmp = Math.round(czestotliwosc);
        czestotliwosc = (double) tmp / factor;

        return czestotliwosc;
    }

    private void utworzPlik(String axis, double tablica[], double czas[]) throws IOException {
        File f = new
                File(Environment.getExternalStorageDirectory()+"/Pomiary/",nazwa+axis+".txt");
        f.createNewFile();
        String napis,accString,accName,accVendor;
        String deviceName = "#Device name: " + DeviceName.getDeviceName() + "\n";
        float accResolution,accRange,accMinDelay;
        endTime = Calendar.getInstance().getTime();
        FileOutputStream fos = new FileOutputStream(f);
        String nazwaPliku = "#Filename: " + nazwa+"\n";
        accMinDelay = mSensor.getMinDelay();
        accResolution = mSensor.getResolution();
        accRange = mSensor.getMaximumRange();
        accVendor = mSensor.getVendor();
        accName = mSensor.getName();
        String opisPomiaru = "#Description: " + opis +"\n";
        String poczatek ="#Start time: " + startTime + "\n";
        String koniec ="#End time: " + endTime + "\n\n\n";
        String realTime = "#Measurement time: " + czas_pomiaru +"s\n";
        String drugaLinia = "         x[mm]          " + "       t[s]         ";
        accString = "#Accelerometer vendor: " + accVendor + "\n" + "#Accelerometer name: " + accName + "\n"  + "#Accelerometer resolution: " +
                    Float.toString(accResolution) + " m/s^2" + "\n" + "#Accelerometer maximum range: " + Float.toString(accRange) + " m/s^2" +
                    "\n" + "#Accelerometer min delay: " + Float.toString(accMinDelay)+ " µm" + "\n\n\n";
        fos.write(nazwaPliku.getBytes());
        fos.write(opisPomiaru.getBytes());
        fos.write(poczatek.getBytes());
        fos.write(koniec.getBytes());
        fos.write(realTime.getBytes());
        fos.write(deviceName.getBytes());
        fos.write(accString.getBytes());
        fos.write(drugaLinia.getBytes());

        for (int i=0; i<rozmiarTablicy; i++) {

            napis = "\n"+df.format(tablica[i])+"   "+czas[i];

            fos.write(napis.getBytes());
        }
        fos.close();
    }



}
