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
public class ChartsSettings {

    private static ChartsSettings chartSettingsInstance = null;
    private boolean gravityForce;
    private int samplingValue;
    private int windowTimeValue;

    public static ChartsSettings getChartsSettings(){
        if(chartSettingsInstance == null){
            chartSettingsInstance = new ChartsSettings();
        }
        return chartSettingsInstance;
    }
}
