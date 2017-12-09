package com.example.jakpe.vibrationdetector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.jakpe.vibrationdetector.interfaces.MainContract;

public class MainActivity extends AppCompatActivity implements MainContract.MainView {

    Toolbar myToolbar ;
    Button nowyPomiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        nowyPomiar = (Button) findViewById(R.id.NowyPomiar);
        final Intent nPomiar = new Intent(this, NowyPomiar.class);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(getString(R.string.main_title));
        getSupportActionBar().setIcon(R.drawable.wave);

        nowyPomiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(nPomiar);
            }
        });


    }



}
