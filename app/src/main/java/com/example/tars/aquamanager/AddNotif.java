package com.example.tars.aquamanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class AddNotif extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notif);

        Button btn_device = (Button) findViewById(R.id.device_select);
        Button btn_trigger = (Button) findViewById(R.id.trigger_select);
        Button btn_alarm = (Button) findViewById(R.id.alarm_select);

        btn_device.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                }
                return true;
            }
        });
    }
}
