package com.example.tars.aquamanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
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

public class AddNotifSelectDevice extends Activity {

    Integer device_ctr;
    String current_checked_device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notif_select_device);

        final TableLayout device_table = (TableLayout) findViewById(R.id.device_table);

        Button device_cancel = (Button) findViewById(R.id.cancel_btn);
        Button device_ok = (Button) findViewById(R.id.ok_btn);

        float d = getBaseContext().getResources().getDisplayMetrics().density;
        int den = (int) d;

        device_ctr = 0;
        current_checked_device = "";

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        Map<String,?> keys = aqua_shared_prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            String key = entry.getKey();
            if (key.startsWith("!dev_") && key.endsWith("_settings")) {
                String device_name = key.substring(5, key.length() - 9);
                device_ctr++;

                RelativeLayout.LayoutParams nmrllp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80*den);
                nmrllp.addRule(RelativeLayout.CENTER_VERTICAL);

                final CheckBox device_checkbox = new CheckBox(this);
                RelativeLayout.LayoutParams params28 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params28.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                device_checkbox.setLayoutParams(params28);
                device_checkbox.setId(1000 + device_ctr);

                device_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            for (int i=0; i<device_ctr; i++) {
                                RelativeLayout rl = (RelativeLayout) device_table.getChildAt(i);
                                CheckBox cb = (CheckBox) rl.getChildAt(0);
                                if (cb.getId() == buttonView.getId()) {
                                    TextView tv = (TextView) rl.getChildAt(1);
                                    current_checked_device = tv.getText().toString();
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

                device_table.addView(sgrl);
            }

            device_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED,returnIntent);
                    finish();
                }
            });

            device_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current_checked_device.isEmpty()) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    } else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", current_checked_device);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            });

            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }
    }
}
