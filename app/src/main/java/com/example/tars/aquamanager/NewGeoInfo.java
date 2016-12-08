package com.example.tars.aquamanager;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class NewGeoInfo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_geo_info);

        final CheckBox circle_checkbox = (CheckBox) findViewById(R.id.circleCheckBox);
        final CheckBox polygon_checkbox = (CheckBox) findViewById(R.id.polygonCheckBox);

        Button confirm = (Button) findViewById(R.id.confirm_geo_button);
        Button cancel = (Button) findViewById(R.id.cancel_geo_button);

        final EditText edit_geo_name = (EditText) findViewById(R.id.edit_geo_name);
        final EditText edit_geo_rad = (EditText) findViewById(R.id.edit_geo_rad);

        circle_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    polygon_checkbox.setChecked(false);
                } else {
                    polygon_checkbox.setChecked(true);
                }
            }
        });

        polygon_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    circle_checkbox.setChecked(false);
                } else {
                    circle_checkbox.setChecked(true);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_geo_name.getText().toString();
                String rad = edit_geo_rad.getText().toString();

                if (name.isEmpty()) Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
                else if (rad.isEmpty()) Toast.makeText(getApplicationContext(), "Please enter a radius", Toast.LENGTH_SHORT).show();
                else if (name.length() > 15) Toast.makeText(getApplicationContext(), "Name too long", Toast.LENGTH_SHORT).show();
                else {
                    if (circle_checkbox.isChecked()) {
                        GeofenceMap.geofenceAddCircleAndName(Double.parseDouble(rad), name);
                        finish();
                    } else if (polygon_checkbox.isChecked()) {
                        GeofenceMap.geofenceAddPolygonAndName(Double.parseDouble(rad), name);
                        finish();
                    }
                }
            }
        });
    }
}
