package com.example.tars.aquamanager;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class AddNotifSelectTrigger extends Activity {

    String current_checked_trigger = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notif_select_trigger);

        Button btn_ok = (Button) findViewById(R.id.ok_btn_trig);
        Button btn_cancel = (Button) findViewById(R.id.cancel_btn_trig);

        final CheckBox lowbatt_cb = (CheckBox) findViewById(R.id.checkBox_1);
        final CheckBox entergeo_cb = (CheckBox) findViewById(R.id.checkBox_2);
        final CheckBox exitgeo_cb = (CheckBox) findViewById(R.id.checkBox_3);
        final CheckBox upload_cb = (CheckBox) findViewById(R.id.checkBox_4);
        final CheckBox seesmac_cb = (CheckBox) findViewById(R.id.checkBox_5);
        final CheckBox startm_cb = (CheckBox) findViewById(R.id.checkBox_6);
        final CheckBox stopm_cb = (CheckBox) findViewById(R.id.checkBox_7);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_checked_trigger.isEmpty()) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", current_checked_trigger);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        lowbatt_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    entergeo_cb.setChecked(false);
                    exitgeo_cb.setChecked(false);
                    upload_cb.setChecked(false);
                    seesmac_cb.setChecked(false);
                    startm_cb.setChecked(false);
                    stopm_cb.setChecked(false);
                    current_checked_trigger = "low battery";
                }
            }
        });

        entergeo_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lowbatt_cb.setChecked(false);
                    exitgeo_cb.setChecked(false);
                    upload_cb.setChecked(false);
                    seesmac_cb.setChecked(false);
                    startm_cb.setChecked(false);
                    stopm_cb.setChecked(false);
                    current_checked_trigger = "enter geofence";
                }
            }
        });

        exitgeo_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    entergeo_cb.setChecked(false);
                    lowbatt_cb.setChecked(false);
                    upload_cb.setChecked(false);
                    seesmac_cb.setChecked(false);
                    startm_cb.setChecked(false);
                    stopm_cb.setChecked(false);
                    current_checked_trigger = "exits geofence";
                }
            }
        });

        upload_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    entergeo_cb.setChecked(false);
                    exitgeo_cb.setChecked(false);
                    lowbatt_cb.setChecked(false);
                    seesmac_cb.setChecked(false);
                    startm_cb.setChecked(false);
                    stopm_cb.setChecked(false);
                    current_checked_trigger = "uploads data";
                }
            }
        });

        seesmac_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    entergeo_cb.setChecked(false);
                    exitgeo_cb.setChecked(false);
                    upload_cb.setChecked(false);
                    lowbatt_cb.setChecked(false);
                    startm_cb.setChecked(false);
                    stopm_cb.setChecked(false);
                    current_checked_trigger = "sees mac address";
                }
            }
        });

        startm_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    entergeo_cb.setChecked(false);
                    exitgeo_cb.setChecked(false);
                    upload_cb.setChecked(false);
                    seesmac_cb.setChecked(false);
                    lowbatt_cb.setChecked(false);
                    stopm_cb.setChecked(false);
                    current_checked_trigger = "starts moving";
                }
            }
        });

        stopm_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    entergeo_cb.setChecked(false);
                    exitgeo_cb.setChecked(false);
                    upload_cb.setChecked(false);
                    seesmac_cb.setChecked(false);
                    startm_cb.setChecked(false);
                    lowbatt_cb.setChecked(false);
                    current_checked_trigger = "stops moving";
                }
            }
        });

    }
}