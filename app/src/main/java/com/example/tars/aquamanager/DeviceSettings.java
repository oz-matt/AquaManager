package com.example.tars.aquamanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONObject;

public class DeviceSettings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_settings);

        SharedPreferences aqua_shared_prefs = this.getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        TextView putAquaID = (TextView) findViewById(R.id.needAquaID);
        TextView putAquaKey = (TextView) findViewById(R.id.needAquaKey);
        TextView putPhoneNo = (TextView) findViewById(R.id.needPhoneNo);

        String name = getIntent().getStringExtra("name");

        try {
            JSONObject qdata = new JSONObject(aqua_shared_prefs.getString("!dev_" + name + "_qdata", "Not Found"));
            putAquaID.setText(qdata.getString("aquaid"));
            putAquaKey.setText(qdata.getString("aquakey"));
            putPhoneNo.setText(qdata.getString("phonenumber"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
