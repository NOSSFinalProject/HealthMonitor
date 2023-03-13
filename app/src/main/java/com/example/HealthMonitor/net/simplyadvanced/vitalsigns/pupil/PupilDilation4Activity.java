package com.example.HealthMonitor.net.simplyadvanced.vitalsigns.pupil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.HealthMonitor.R;


public class PupilDilation4Activity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pupil_dilation4);

        Button pdn = (Button) findViewById(R.id.PDnext4);
        pdn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
