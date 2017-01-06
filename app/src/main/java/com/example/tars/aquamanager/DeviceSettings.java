package com.example.tars.aquamanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DeviceSettings extends Activity {

    int spinnerctr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        float d = getBaseContext().getResources().getDisplayMetrics().density;
        int den = (int) d;

        spinnerctr = 0;

        final SharedPreferences aqua_shared_prefs = this.getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        TextView putName = (TextView) findViewById(R.id.di_name_tve);
        TextView putBattery = (TextView) findViewById(R.id.di_battery_tve);

        TextView putCurrentLocation = (TextView) findViewById(R.id.di_currentlocation_tve);
        TextView putPreviousLocation = (TextView) findViewById(R.id.di_previouslocation_tve);

        TextView putAquaID = (TextView) findViewById(R.id.di_aquaid_tve);
        TextView putAquaKey = (TextView) findViewById(R.id.di_aquakey_tve);
        TextView putPhoneNo = (TextView) findViewById(R.id.di_phonenumber_tve);

        TextView putTemp = (TextView) findViewById(R.id.di_temp_tve);
        TextView putHumidity = (TextView) findViewById(R.id.di_humidity_tve);
        TextView putHeight = (TextView) findViewById(R.id.di_height_tve);
        TextView putSpeed = (TextView) findViewById(R.id.di_speed_tve);
        TextView putDir = (TextView) findViewById(R.id.di_dir_tve);
        TextView putNumSats = (TextView) findViewById(R.id.di_nums_tve);

        TextView putCurLoc = (TextView) findViewById(R.id.curloc_tve);
        TextView putPrevLoc = (TextView) findViewById(R.id.prevloc_tve);

        final String name = getIntent().getStringExtra("name");

        String[] fullsettingsblk = aqua_shared_prefs.getString("!dev_" + name + "_fullsettings", "Not Found").split("!");

        String loc_str_long = fullsettingsblk[0];
        String temp = fullsettingsblk[1];
        String humidity = fullsettingsblk[2];
        String height = fullsettingsblk[3];
        String speed = fullsettingsblk[4];
        String direction = fullsettingsblk[5];
        String nums = fullsettingsblk[6];

        String latest_time = fullsettingsblk[7];
        String second_latest_time = fullsettingsblk[8];

        putCurLoc.setText("Current Location\r\n(" + latest_time + ")");
        putPrevLoc.setText("Previous Location\r\n(" + second_latest_time + ")");

        Spinner colorspinner = (Spinner) findViewById(R.id.di_spinner);

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Red");
        spinnerArray.add("Blue");
        spinnerArray.add("Green");
        spinnerArray.add("Orange");
        spinnerArray.add("Violet");
        spinnerArray.add("Rose");
        spinnerArray.add("Magenta");
        spinnerArray.add("Azure");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);

        colorspinner.setPadding(8*den, 8*den, 8*den, 8*den);
        colorspinner.setAdapter(spinnerArrayAdapter);

        try {
            JSONObject qdata = new JSONObject(aqua_shared_prefs.getString("!dev_" + name + "_qdata", "Not Found"));

            putName.setText(name);

            String bat = aqua_shared_prefs.getString("!dev_" + name + "_pctbat", "Not Found") + "%";
            putBattery.setText(bat);

            putCurrentLocation.setText(loc_str_long);
            putPreviousLocation.setText(aqua_shared_prefs.getString("!dev_" + name + "_prevlocstrlong", "Not Found"));

            putAquaID.setText(qdata.getString("aquaid"));
            putAquaKey.setText(qdata.getString("aquakey"));
            putPhoneNo.setText(qdata.getString("phonenumber"));

            putTemp.setText(temp);
            putHumidity.setText(humidity);
            putHeight.setText(height);
            putSpeed.setText(speed);
            putDir.setText(direction);
            putNumSats.setText(nums);


            final JSONObject qsettings = new JSONObject(aqua_shared_prefs.getString("!dev_" + name + "_settings", "Not Found"));
            String markerColor = qsettings.getString("MarkerColor");

            if (markerColor.equalsIgnoreCase("RED")) colorspinner.setSelection(0);
            else if (markerColor.equalsIgnoreCase("BLUE")) colorspinner.setSelection(1);
            else if (markerColor.equalsIgnoreCase("GREEN")) colorspinner.setSelection(2);
            else if (markerColor.equalsIgnoreCase("ORANGE")) colorspinner.setSelection(3);
            else if (markerColor.equalsIgnoreCase("VIOLET")) colorspinner.setSelection(4);
            else if (markerColor.equalsIgnoreCase("ROSE")) colorspinner.setSelection(5);
            else if (markerColor.equalsIgnoreCase("MAGENTA")) colorspinner.setSelection(6);
            else if (markerColor.equalsIgnoreCase("AZURE")) colorspinner.setSelection(7);

            colorspinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    spinnerctr++;
                    return false;
                }
            });

            colorspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (spinnerctr > 0) {
                        switch (position) {
                            case 0:
                                try {
                                    qsettings.remove("MarkerColor");
                                    qsettings.put("MarkerColor", "RED");
                                    aqua_shared_prefs.edit().remove("!dev_" + name + "_settings").apply();
                                    aqua_shared_prefs.edit().putString("!dev_" + name + "_settings", qsettings.toString()).apply();
                                    Toast.makeText(getBaseContext(), "Marker color changed to red", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "JSON Exception 108", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1:
                                try {
                                    qsettings.remove("MarkerColor");
                                    qsettings.put("MarkerColor", "BLUE");
                                    aqua_shared_prefs.edit().remove("!dev_" + name + "_settings").apply();
                                    aqua_shared_prefs.edit().putString("!dev_" + name + "_settings", qsettings.toString()).apply();
                                    Toast.makeText(getBaseContext(), "Marker color changed to blue", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "JSON Exception 108", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2:
                                try {
                                    qsettings.remove("MarkerColor");
                                    qsettings.put("MarkerColor", "GREEN");
                                    aqua_shared_prefs.edit().remove("!dev_" + name + "_settings").apply();
                                    aqua_shared_prefs.edit().putString("!dev_" + name + "_settings", qsettings.toString()).apply();
                                    Toast.makeText(getBaseContext(), "Marker color changed to green", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "JSON Exception 108", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 3:
                                try {
                                    qsettings.remove("MarkerColor");
                                    qsettings.put("MarkerColor", "ORANGE");
                                    aqua_shared_prefs.edit().remove("!dev_" + name + "_settings").apply();
                                    aqua_shared_prefs.edit().putString("!dev_" + name + "_settings", qsettings.toString()).apply();
                                    Toast.makeText(getBaseContext(), "Marker color changed to orange", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "JSON Exception 108", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 4:
                                try {
                                    qsettings.remove("MarkerColor");
                                    qsettings.put("MarkerColor", "VIOLET");
                                    aqua_shared_prefs.edit().remove("!dev_" + name + "_settings").apply();
                                    aqua_shared_prefs.edit().putString("!dev_" + name + "_settings", qsettings.toString()).apply();
                                    Toast.makeText(getBaseContext(), "Marker color changed to violet", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "JSON Exception 108", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 5:
                                try {
                                    qsettings.remove("MarkerColor");
                                    qsettings.put("MarkerColor", "ROSE");
                                    aqua_shared_prefs.edit().remove("!dev_" + name + "_settings").apply();
                                    aqua_shared_prefs.edit().putString("!dev_" + name + "_settings", qsettings.toString()).apply();
                                    Toast.makeText(getBaseContext(), "Marker color changed to rose", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "JSON Exception 108", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 6:
                                try {
                                    qsettings.remove("MarkerColor");
                                    qsettings.put("MarkerColor", "MAGENTA");
                                    aqua_shared_prefs.edit().remove("!dev_" + name + "_settings").apply();
                                    aqua_shared_prefs.edit().putString("!dev_" + name + "_settings", qsettings.toString()).apply();
                                    Toast.makeText(getBaseContext(), "Marker color changed to magenta", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "JSON Exception 108", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 7:
                                try {
                                    qsettings.remove("MarkerColor");
                                    qsettings.put("MarkerColor", "AZURE");
                                    aqua_shared_prefs.edit().remove("!dev_" + name + "_settings").apply();
                                    aqua_shared_prefs.edit().putString("!dev_" + name + "_settings", qsettings.toString()).apply();
                                    Toast.makeText(getBaseContext(), "Marker color changed to azure", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getBaseContext(), "JSON Exception 108", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
