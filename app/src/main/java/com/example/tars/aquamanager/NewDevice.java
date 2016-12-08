package com.example.tars.aquamanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class NewDevice extends Activity {

    static HttpURLConnection urlConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);
        ((TextView)((LinearLayout)((ViewGroup) getWindow().getDecorView()).getChildAt(0)).getChildAt(0)).setGravity(Gravity.CENTER);

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        Button submitButton = (Button) findViewById(R.id.submit_button);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);

        final EditText name_edit = (EditText) findViewById(R.id.name_edit);

        if(!getIntent().hasExtra("qdata")) {
            Toast.makeText(getBaseContext(), "Error: No data", Toast.LENGTH_SHORT).show();
            finish();
        }

        //final String qdata = getIntent().getStringExtra("qdata");
        //final String aqsens = getIntent().getStringExtra("aqsens");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_edit.getText().toString();

                if (name.equals(null)) {
                    Toast.makeText(getBaseContext(), "Enter a name", Toast.LENGTH_SHORT).show();
                }

                if (isNameGood(name, aqua_shared_prefs)) {

                    JSONObject settings_obj = new JSONObject();
                    try {
                        settings_obj.put("DisplayFlag", "true");
                        String[] id_and_color = getMarkerIdAndColor(aqua_shared_prefs);
                        settings_obj.put("Id", id_and_color[0]);
                        settings_obj.put("MarkerColor", id_and_color[1]);
                        settings_obj.put("MaxMarkersToDisplay", "200");

                        String settings_str = settings_obj.toString();

                        String qdata = getIntent().getStringExtra("qdata");
                        String aqsens = getIntent().getStringExtra("aqsens");
                        String loc = getIntent().getStringExtra("loc");
                        String batt = getIntent().getStringExtra("batt");

                        Log.d("set", settings_str);

                        aqua_shared_prefs.edit().putString("!dev_" + name + "_qdata", qdata).apply();
                        aqua_shared_prefs.edit().putString("!dev_" + name + "_aqsens", aqsens).apply();
                        aqua_shared_prefs.edit().putString("!dev_" + name + "_locstr", loc).apply();
                        aqua_shared_prefs.edit().putString("!dev_" + name + "_pctbat", batt).apply();
                        aqua_shared_prefs.edit().putString("!dev_" + name + "_settings", settings_str).apply();

                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "JSON Settings Failed", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Name already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isNameGood(String name, SharedPreferences prefs) {

        String proposed_name = "!dev_" + name + "_qdata";

        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            if (entry.getKey().equalsIgnoreCase(proposed_name)) return false;
        }

        return true;
    }

    private void printPrefs(SharedPreferences prefs) {

        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    private String[] getMarkerIdAndColor(SharedPreferences prefs) {
        int id = 0;
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().endsWith("settings")) id++;
        }
        String color = "YELLOW";
        if ((id % 8) == 0) color = "RED";
        if ((id % 8) == 1) color = "BLUE";
        if ((id % 8) == 2) color = "GREEN";
        if ((id % 8) == 3) color = "ORANGE";
        if ((id % 8) == 4) color = "VIOLET";
        if ((id % 8) == 5) color = "ROSE";
        if ((id % 8) == 6) color = "MAGENTA";
        if ((id % 8) == 7) color = "AZURE";
        String[] ret = {String.valueOf(id), color};
        return ret;
    }
}