package com.example.tars.aquamanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class AddNotif extends Activity {

    static final int GET_DEVICE_FOR_NOTIF = 1;
    static final int GET_TRIGGER_FOR_NOTIF = 2;

    TextView tv_device;
    TextView tv_trigger;
    TextView tv_alarm;
    TextView notif_sentence;

    String dev = "";
    String trig = "";
    String alm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notif);

        Button btn_device = (Button) findViewById(R.id.device_select);
        Button btn_trigger = (Button) findViewById(R.id.trigger_select);
        Button btn_alarm = (Button) findViewById(R.id.alarm_select);

        tv_device = (TextView) findViewById(R.id.device_tv);
        tv_trigger = (TextView) findViewById(R.id.trigger_tv);
        tv_alarm = (TextView) findViewById(R.id.alarm_tv);
        notif_sentence = (TextView) findViewById(R.id.notif_sen);

        btn_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNotif.this, AddNotifSelectDevice.class);
                startActivityForResult(intent, GET_DEVICE_FOR_NOTIF);
            }
        });

        btn_trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNotif.this, AddNotifSelectTrigger.class);
                startActivityForResult(intent, GET_TRIGGER_FOR_NOTIF);
            }
        });

    }

    private String getNewNotifSentence() {
        String dev_buf;
        String trig_buf;
        String alm_buf;

        if (dev.isEmpty()) dev_buf = "<?>";
            else dev_buf = dev;
        if (trig.isEmpty()) trig_buf = "<?>";
            else trig_buf = trig;
        if (alm.isEmpty()) alm_buf = "<?>";
            else alm_buf = alm;

        String full_sentence = "When " + dev_buf + " enters " + trig_buf + ", send <?> a " + alm_buf;

        return full_sentence;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_DEVICE_FOR_NOTIF) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                tv_device.setText("Device: " + result);
                dev = result;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        } else if (requestCode == GET_TRIGGER_FOR_NOTIF) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                tv_trigger.setText("Trigger: " + result);
                trig = result;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }

        notif_sentence.setText(getNewNotifSentence());

    }
}
