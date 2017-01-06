package com.example.tars.aquamanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class ViewRawData extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_raw_data);

        TextView putJson = (TextView) findViewById(R.id.putJson);

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        String need_pref = getIntent().getStringExtra("aqsens_pref");
        Integer aqsen_ele = getIntent().getIntExtra("aqsens_ele", 0);

        String rawaqsensstr = aqua_shared_prefs.getString(need_pref, "Not Found");

        try {
            JSONArray aqsens_json_array = new JSONArray(rawaqsensstr);
            JSONObject single_aqsen = aqsens_json_array.getJSONObject(aqsen_ele);

            putJson.setText(single_aqsen.toString(2));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "AQSEN Parse Error 102", Toast.LENGTH_SHORT).show();
        }
    }
}
