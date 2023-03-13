package com.example.HealthMonitor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.HealthMonitor.net.simplyadvanced.vitalsigns.bloodpressure.BloodPressureActivity;

import androidx.appcompat.app.AppCompatActivity;

public class StartVitalSigns extends AppCompatActivity {
    private int p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_vital_signs);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            p = extras.getInt("Page");
        }

        Button vibutton = this.findViewById(R.id.vibutton);

        vibutton.setOnClickListener(v -> {

            //switch is to decide which activity must be opened
            switch (p) {

                case 0: {
                    Intent i = new Intent(v.getContext(), SpO2Calc.class);
                    startActivity(i);
                    finish();
                }
                break;

                case 1: {
                    Intent i = new Intent(v.getContext(), HeartRateCalc.class);
                    startActivity(i);
                    finish();
                }
                break;

                case 2: {
                    Intent i = new Intent(v.getContext(), BloodPressureActivity.class);
                    startActivity(i);
                    finish();
                }
                break;

                case 3: {
                    Intent i = new Intent(v.getContext(), AllVitalsCalc.class);
                    startActivity(i);
                    finish();
                }
                break;


            }

        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(StartVitalSigns.this, HeathCheckMenu.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }



}