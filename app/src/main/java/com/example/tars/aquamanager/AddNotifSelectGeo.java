package com.example.tars.aquamanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Map;

public class AddNotifSelectGeo extends Activity {

    Integer geo_ctr;
    String current_checked_geo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notif_select_geo);

        final TableLayout geo_table = (TableLayout) findViewById(R.id.sel_geo_table);

        Button geo_cancel = (Button) findViewById(R.id.cancel_btn_sel_geo);
        Button geo_ok = (Button) findViewById(R.id.ok_btn_sel_geo);

        float d = getBaseContext().getResources().getDisplayMetrics().density;
        int den = (int) d;

        geo_ctr = 0;
        current_checked_geo = "";

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        Map<String,?> keys = aqua_shared_prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            String key = entry.getKey();
            if (key.startsWith("!geo_") && key.endsWith("_settings")) {
                String device_name = key.substring(5, key.length() - 9);
                geo_ctr++;

                RelativeLayout.LayoutParams nmrllp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80*den);
                nmrllp.addRule(RelativeLayout.CENTER_VERTICAL);

                final CheckBox device_checkbox = new CheckBox(this);
                RelativeLayout.LayoutParams params28 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params28.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params28.addRule(RelativeLayout.CENTER_VERTICAL);
                device_checkbox.setLayoutParams(params28);
                device_checkbox.setId(1000 + geo_ctr);

                device_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            for (int i=0; i<geo_ctr; i++) {
                                RelativeLayout rl = (RelativeLayout) geo_table.getChildAt(i);
                                CheckBox cb = (CheckBox) rl.getChildAt(0);
                                if (cb.getId() == buttonView.getId()) {
                                    TextView tv = (TextView) rl.getChildAt(1);
                                    current_checked_geo = tv.getText().toString();
                                } else {
                                    cb.setChecked(false);
                                }
                            }
                        }
                    }
                });

                RelativeLayout.LayoutParams paramsDevText = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsDevText.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                paramsDevText.addRule(RelativeLayout.CENTER_VERTICAL);

                TextView device_tv = new TextView(this);
                device_tv.setText(device_name);
                device_tv.setPadding(8*den, 8*den, 8*den, 8*den);
                device_tv.setTextSize(16);
                device_tv.setTextColor(Color.rgb(240,240,240));
                RelativeLayout.LayoutParams device_tv_lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                device_tv_lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                device_tv.setLayoutParams(paramsDevText);

                RelativeLayout sgrl = new RelativeLayout(this);
                RelativeLayout.LayoutParams sgrllp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80*den);
                sgrllp.addRule(RelativeLayout.CENTER_VERTICAL);
                sgrl.setLayoutParams(nmrllp);
                sgrl.addView(device_checkbox);
                sgrl.addView(device_tv);

                geo_table.addView(sgrl);
            }

            geo_table.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED,returnIntent);
                    finish();
                }
            });

            geo_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current_checked_geo.isEmpty()) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    } else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", current_checked_geo);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            });

            geo_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            });

            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }
    }
}
