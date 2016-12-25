package com.example.tars.aquamanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Map;

public class AddNotifSelectTrigger extends Activity {

    private int current_checked_trigger = 0;

    static final int LOW_BATTERY = 1;
    static final int ENTER_GEOFENCE = 2;
    static final int EXIT_GEOFENCE = 3;
    static final int UPLOADS_DATA = 4;
    static final int SEES_MAC = 5;
    static final int STARTS_MOVING = 6;
    static final int STOPS_MOVING = 7;

    Context context;

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

        context = this;

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
                if (current_checked_trigger == 0) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                } else {

                    switch (current_checked_trigger) {
                        case LOW_BATTERY :
                            Intent returnIntentLb = new Intent();
                            returnIntentLb.putExtra("result", "low battery");
                            setResult(Activity.RESULT_OK, returnIntentLb);
                            finish();
                            break;
                        case ENTER_GEOFENCE :
                            if (haveAtLeastOneGeofence()) {
                                Intent getentergeo = new Intent(AddNotifSelectTrigger.this, AddNotifSelectGeo.class);
                                startActivityForResult(getentergeo, ENTER_GEOFENCE);
                            } else {


                            }
                            break;
                        case EXIT_GEOFENCE :
                            if (haveAtLeastOneGeofence()) {
                                Intent getexitgeo = new Intent(AddNotifSelectTrigger.this, AddNotifSelectGeo.class);
                                startActivityForResult(getexitgeo, EXIT_GEOFENCE);
                            } else {
                        new AlertDialog.Builder(context)
                                .setTitle("No Geofence")
                                .setMessage("Create at least one geofence to use this function")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                            }
                            break;
                        case UPLOADS_DATA :
                            Intent returnIntentUl = new Intent();
                            returnIntentUl.putExtra("result", "uploads data");
                            setResult(Activity.RESULT_OK, returnIntentUl);
                            finish();
                            break;
                        case SEES_MAC :
                            Intent getmac = new Intent(AddNotifSelectTrigger.this, AddNotifSelectMac.class);
                            startActivityForResult(getmac, SEES_MAC);
                            break;
                        case STARTS_MOVING :
                            Intent returnIntentStartsMoving = new Intent();
                            returnIntentStartsMoving.putExtra("result", "starts moving");
                            setResult(Activity.RESULT_OK, returnIntentStartsMoving);
                            finish();
                            break;
                        case STOPS_MOVING :
                            Intent returnIntentStopsMoving = new Intent();
                            returnIntentStopsMoving.putExtra("result", "stops moving");
                            setResult(Activity.RESULT_OK, returnIntentStopsMoving);
                            finish();
                            break;
                    }

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
                    current_checked_trigger = LOW_BATTERY;
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
                    current_checked_trigger = ENTER_GEOFENCE;
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
                    current_checked_trigger = EXIT_GEOFENCE;
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
                    current_checked_trigger = UPLOADS_DATA;
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
                    current_checked_trigger = SEES_MAC;
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
                    current_checked_trigger = STARTS_MOVING;
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
                    current_checked_trigger = STOPS_MOVING;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ENTER_GEOFENCE:
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getStringExtra("result");
                    String full_trigger_result = "enters \"" + result + "\"";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", full_trigger_result);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                }
            case EXIT_GEOFENCE:
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getStringExtra("result");
                    String full_trigger_result = "exits \"" + result + "\"";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", full_trigger_result);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else if (resultCode == Activity.RESULT_CANCELED) {

                }
            case SEES_MAC:
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getStringExtra("result");
                    String full_trigger_result = result;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", full_trigger_result);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else if (resultCode == Activity.RESULT_CANCELED) {

                }
        }
    }

    private boolean haveAtLeastOneGeofence() {
        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        Map<String,?> keys = aqua_shared_prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("!geo_") && key.endsWith("_settings")) {
                return true;
            }
        }
        return false;
    }
}