package com.example.jakpe.vibrationdetector;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.example.jakpe.vibrationdetector.settings.AcquisitionSettings;
import com.jaredrummler.android.device.DeviceName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Pernal on 2/15/2018.
 */

public class FileSaver {

    public static Date measurementStartDate;
    public static Sensor sensor;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public static void saveData(double xAxis[], double yAxis[], double zAxis[]) throws IOException {
        String fileName = AcquisitionSettings.getFileName() + "_" + AcquisitionSettings.fileCounter +".txt";
        String fileNameString = "#Filename: " + fileName+"\n";
        double samplingTime =(double) 1/AcquisitionSettings.getSamplingFrequency();
        String samplingTimeString ="#Sampling Time: " + Double.toString(samplingTime)+"\n";

        String measurementDescription = "#Description: " + AcquisitionSettings.getDescription() +"\n";
        String measurementStartTime ="#Start time: " + measurementStartDate + "\n";

        Date endTime = Calendar.getInstance().getTime();
        String endTimeString ="#End time: " + endTime + "\n\n\n";
        String measurementTime = "#Measurement time: " + AcquisitionSettings.getMeasurementTime() +"s\n";

        int accMinDelay = sensor.getMinDelay();
        float accResolution = sensor.getResolution();
        float accRange = sensor.getMaximumRange();
        String accVendor = sensor.getVendor();
        String accName = sensor.getName();
        String line;
        String deviceName = "#Device name: " + DeviceName.getDeviceName() + "\n";

        String dir = Environment.getExternalStorageDirectory()+File.separator+"VibrationDetectorMeasurements";

        File folder = new File(dir);
        folder.mkdirs();

        File file = new File(dir, fileName);
        file.createNewFile();

        FileOutputStream fos = new FileOutputStream(file);

        String header = "         x[m/s^2]          " + "       y[m/s^2]         " + "         z[m/s^2]";
        String accParameters = "#Accelerometer vendor: " + accVendor + "\n" + "#Accelerometer name: " + accName + "\n"  + "#Accelerometer resolution: " +
                Float.toString(accResolution) + " m/s^2" + "\n" + "#Accelerometer maximum range: " + Float.toString(accRange) + " m/s^2" +
                "\n" + "#Accelerometer min delay: " + Integer.toString(accMinDelay)+ " µs" + "\n\n\n";
        fos.write(fileNameString.getBytes());
        fos.write(measurementDescription.getBytes());
        fos.write(measurementStartTime.getBytes());
        fos.write(endTimeString.getBytes());
        fos.write(measurementTime.getBytes());
        fos.write(samplingTimeString.getBytes());
        fos.write(deviceName.getBytes());
        fos.write(accParameters.getBytes());
        fos.write(header.getBytes());

        for (int i=0; i<xAxis.length; i++) {
            line = "\n"+xAxis[i]+"   "+yAxis[i]+"   "+zAxis[i];
            fos.write(line.getBytes());
        }
        fos.close();
    }

}
