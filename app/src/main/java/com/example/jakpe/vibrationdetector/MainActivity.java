package com.example.jakpe.vibrationdetector;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jakpe.vibrationdetector.implementations.MainPresenterImpl;
import com.example.jakpe.vibrationdetector.interfaces.MainContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainContract.MainView {

    @BindView(R.id.my_toolbar) Toolbar mainToolbar ;
    @BindView(R.id.new_measurement_button) Button newMeasurementButton;
    @BindView(R.id.window_time_edit_text) EditText windowEditText;
    MainPresenterImpl mainPresenter = new MainPresenterImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initUi();
        initPresenter();

    }

    private void initPresenter(){
        mainPresenter.attach(this);
    }

    private void initUi(){

        setSupportActionBar(mainToolbar);
        ActionBar mainActionBar = getSupportActionBar();

        if(mainActionBar!=null){
            mainActionBar.setTitle(getString(R.string.main_title));
            mainActionBar.setIcon(R.drawable.wave);
        }

        newMeasurementButton.setVisibility(View.INVISIBLE);


        newMeasurementButton.setOnClickListener(v -> mainPresenter.newMeasurementClicked());

        windowEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mainPresenter.validateText(editable);
            }
        });

    }

    @Override
    protected void onDestroy() {
        mainPresenter.detach();
        super.onDestroy();
    }

    @Override
    public void startNewMeasurementActivity() {
        final Intent newMeasurementActivity = new Intent(this, NewMeasurement.class);
        int number = Integer.valueOf(windowEditText.getText().toString());
        newMeasurementActivity.putExtra("Window Time", number);

        startActivity(newMeasurementActivity);
    }

    @Override
    public void onInvalidInput(String numberInString) {
        Toast.makeText(this, "You typed: " + numberInString + " so far, the maximum window size is 10 seconds", Toast.LENGTH_SHORT).show();
        newMeasurementButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onValidInput() {
        newMeasurementButton.setVisibility(View.VISIBLE);
    }
}
