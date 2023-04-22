package com.example.HealthMonitor;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;

public class HeathCheckMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heath_check_menu);
        GridView healthmenu;
        healthmenu = findViewById(R.id.idGVcourses);
        ArrayList<CourseModel> courseModelArrayList = new ArrayList<CourseModel>();
        courseModelArrayList.add(new CourseModel("SpO2", R.mipmap.oxi1));
        courseModelArrayList.add(new CourseModel("Heart Rate", R.mipmap.hricon));
        courseModelArrayList.add(new CourseModel("Temperature", R.mipmap.tempicon));
        courseModelArrayList.add(new CourseModel("All Vitals", R.mipmap.vitalsicon));
        GridViewAdapter adapter = new GridViewAdapter (this, courseModelArrayList);
        healthmenu.setAdapter(adapter);
        healthmenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent = new Intent(getApplicationContext(),StartVitalSigns.class);
                    intent.putExtra("Page",position);
                    startActivity(intent);
                    finish();
                }
                if(position==1){
                    Intent intent = new Intent(getApplicationContext(),StartVitalSigns.class);
                    intent.putExtra("Page",position);
                    startActivity(intent);
                    finish();
                }
                if(position==2){
                    Intent intent = new Intent(getApplicationContext(),TempCalc.class);
                    intent.putExtra("Page",position);
                    startActivity(intent);
                    finish();
                }
                if(position==3){
                    Intent intent = new Intent(getApplicationContext(),StartVitalSigns.class);
                    intent.putExtra("Page",position);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
