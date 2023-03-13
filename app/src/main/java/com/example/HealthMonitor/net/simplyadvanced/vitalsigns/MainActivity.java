package com.example.HealthMonitor.net.simplyadvanced.vitalsigns;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.HealthMonitor.R;
import com.example.HealthMonitor.net.simplyadvanced.vitalsigns.bloodpressure.BloodPressureActivity;
import com.example.HealthMonitor.net.simplyadvanced.vitalsigns.bodytemperature.BodyTemperatureActivity;
import com.example.HealthMonitor.net.simplyadvanced.vitalsigns.facialgestures.FacialGesturesActivity;
import com.example.HealthMonitor.net.simplyadvanced.vitalsigns.multiplefaces.MultipleFacesDetectionActivity;
import com.example.HealthMonitor.net.simplyadvanced.vitalsigns.oxygensaturation.OxygenSaturationActivity;
import com.example.HealthMonitor.net.simplyadvanced.vitalsigns.pupil.PupilDilationActivity;
import com.example.HealthMonitor.net.simplyadvanced.vitalsigns.respiratoryrate.RespiratoryRateActivity;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout rootView = (LinearLayout) findViewById(R.id.root);

        addActivityNavigationView(rootView, CheckVitalSignsActivity.class, "HR/BP/Temp w/Hardcoded Forehead Area");
        addActivityNavigationView(rootView, BloodPressureActivity.class, "HR/BP/Temp w/Face Detection");
        addActivityNavigationView(rootView, BodyTemperatureActivity.class, "[N/A] Auto HR/BP at 5fps");
        addActivityNavigationView(rootView, RespiratoryRateActivity.class, "Respiratory Rate");
        addActivityNavigationView(rootView, OxygenSaturationActivity.class, "[SIM] Oxygen Saturation");
        addActivityNavigationView(rootView, FacialGesturesActivity.class, "Locate Facial Features");
        addActivityNavigationView(rootView, PupilDilationActivity.class, "[SIM] Pupils");
        addActivityNavigationView(rootView, MultipleFacesDetectionActivity.class, "Detect Multiple Faces");
    }

    private void addActivityNavigationView(ViewGroup root, final Class<?> activityClass,
            String title) {
        Button button = new Button(this);
        button.setText(title);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, activityClass));
            }
        });
        root.addView(button);
    }

}
