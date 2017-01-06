package com.example.tars.aquamanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class NotifSettings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_settings);

        String num_ntf = getIntent().getStringExtra("ntf_num");

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        TextView ni_devicename = (TextView) findViewById(R.id.ni_devicename_tve);
        TextView ni_aquakey = (TextView) findViewById(R.id.di_aquakey_tve);
        TextView ni_trigger = (TextView) findViewById(R.id.di_phonenumber_tve);
        TextView ni_geofence = (TextView) findViewById(R.id.ni_geofence_tve);

        TextView ni_alert = (TextView) findViewById(R.id.ni_alert_tve);
        TextView ni_alerttarget = (TextView) findViewById(R.id.ni_alerttarget_tve);

        TextView ni_id = (TextView) findViewById(R.id.ni_id_tve);

        try {
            JSONObject ntf_data = new JSONObject(aqua_shared_prefs.getString("!ntf_" + num_ntf + "_data", "Not Found"));

            ni_devicename.setText(ntf_data.getString("aquaname"));
            ni_aquakey.setText(aqua_shared_prefs.getString("!ntf_" + num_ntf + "_tkey", "Not Found"));
            ni_trigger.setText(ntf_data.getString("trigger"));

            if (ntf_data.has("geoname")) {
                ni_geofence.setText(ntf_data.getString("geoname"));
            } else {
                ni_geofence.setText("--");
            }

            ni_alert.setText(ntf_data.getString("alert"));
            ni_alerttarget.setText(ntf_data.getString("target"));
            ni_id.setText(ntf_data.getString("ntfuuid"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "JSON Exception 106", Toast.LENGTH_SHORT).show();
        }
    }
}
