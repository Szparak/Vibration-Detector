/**
 Klasa odpowiedzialna za przetrzymywanie globalnych ustawień
 **/
package com.example.jakpe.vibrationdetector.settings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AcquisitionSettings {

    private String fileName;
    private String description;
    private int samplingFrequency;
    private int measurementTime;
    private int fileCounter;
    private static AcquisitionSettings acquisitionSettingsInstance;

    public static AcquisitionSettings getAcquisitionSettings(){
        if(acquisitionSettingsInstance == null){
            acquisitionSettingsInstance = new AcquisitionSettings();
        }
        return acquisitionSettingsInstance;
    }

    public void incrementFileCounter(){
        fileCounter++;
    }
}
