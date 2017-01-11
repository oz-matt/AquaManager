package com.example.tars.aquamanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

public class AddNotif extends Activity {

    private Activity a = this;

    static final int GET_DEVICE_FOR_NOTIF = 1;
    static final int GET_TRIGGER_FOR_NOTIF = 2;
    static final int GET_ALARM_FOR_NOTIF = 3;

    boolean continuous = true;

    TextView tv_device;
    TextView tv_trigger;
    TextView tv_alarm;
    TextView notif_sentence;

    String dev = "";
    String trig = "";
    String alm = "";
    String trg = "";

    String triggerType = "Unk";
    String geo = "Unk";
    String mac = "Unk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notif);

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        Button btn_device = (Button) findViewById(R.id.device_select);
        Button btn_trigger = (Button) findViewById(R.id.trigger_select);
        Button btn_alarm = (Button) findViewById(R.id.alarm_select);

        Button cancel_btn = (Button) findViewById(R.id.cancel_button_an);
        Button ok_btn = (Button) findViewById(R.id.submit_button_an);

        final CheckBox continuous_cb = (CheckBox) findViewById(R.id.continuous_cb);
        final CheckBox onChange_cb = (CheckBox) findViewById(R.id.onChange_cb);

        ImageButton info_ib = (ImageButton) findViewById(R.id.info_ib);

        info_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AddNotif.this)
                        .setTitle("On Change")
                        .setMessage("This option will set the notification to only send an alarm when the target Aqua changes from a non-triggered state to a triggered one.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        continuous_cb.setChecked(true);

        continuous_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onChange_cb.setChecked(false);
                    continuous = true;
                    notif_sentence.setText(getNewNotifSentenceAndUpdateServerVars());
                }
            }
        });

        onChange_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    continuous_cb.setChecked(false);
                    continuous = false;
                    notif_sentence.setText(getNewNotifSentenceAndUpdateServerVars());
                }
            }
        });

        tv_device = (TextView) findViewById(R.id.device_tv);
        tv_trigger = (TextView) findViewById(R.id.trigger_tv);
        tv_alarm = (TextView) findViewById(R.id.alarm_tv);
        notif_sentence = (TextView) findViewById(R.id.notif_sen);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dev.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please select a device", Toast.LENGTH_SHORT).show();
                } else if (trig.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please select a trigger", Toast.LENGTH_SHORT).show();
                } else if (alm.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please select an alarm", Toast.LENGTH_SHORT).show();
                } else if (trg.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Please select a target", Toast.LENGTH_SHORT).show();
                } else {
                    //Good Notification
                    final JSONObject outgoing_json = new JSONObject();

                    final String iid = aqua_shared_prefs.getString("iid", "Not Found");
                    final String dev_qdata = aqua_shared_prefs.getString("!dev_" + dev + "_qdata", "Not Found");

                    if (dev_qdata.equalsIgnoreCase("Not Found")) {
                        Toast.makeText(getBaseContext(), "101: Device data missing", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            final JSONObject dev_qdata_json = new JSONObject(dev_qdata);
                            final String aquakey = dev_qdata_json.getString("aquakey");

                            final String uuid = UUID.randomUUID().toString().substring(0,8);

                            final JSONObject notif_data = new JSONObject();
                            notif_data.put("alert", alm);
                            notif_data.put("aquaname", dev);
                            notif_data.put("ntfuuid", uuid);
                            notif_data.put("target", trg);
                            notif_data.put("trigger", triggerType);
                            notif_data.put("continuous", String.valueOf(continuous_cb.isChecked()));
                            //notif_data.put("anid", "12312351235");
                            if ((triggerType.equalsIgnoreCase("entersGeo")) || (triggerType.equalsIgnoreCase("exitsGeo"))) {
                                String geo_settings = aqua_shared_prefs.getString("!geo_" + geo + "_settings", "Not Found");
                                Log.d("gotit", geo_settings);
                                Log.d("gotit", geo);

                                final JSONObject geo_settings_json = new JSONObject(geo_settings);

                                String type = geo_settings_json.get("geotype").toString();

                                if (type.equalsIgnoreCase("circle")) {
                                    String data = geo_settings_json.get("geodata").toString();
                                    notif_data.put("geotype", type);
                                    notif_data.put("geodata", data);
                                    notif_data.put("geoname", geo);
                                } else {
                                    final JSONArray geo_poly_array = new JSONArray(geo_settings_json.get("geodata").toString());

                                    notif_data.put("geotype", type);
                                    notif_data.put("geodata", geo_poly_array);
                                    notif_data.put("geoname", geo);
                                }


                            } else if (triggerType.equalsIgnoreCase("seesMac")) {
                                notif_data.put("macaddress", mac);
                            }

                            outgoing_json.put("reqtype", "notif");
                            outgoing_json.put("iid", iid);
                            outgoing_json.put("aquakey", aquakey);
                            outgoing_json.put("data", notif_data);
                            outgoing_json.put("continuous", String.valueOf(continuous_cb.isChecked()));

                            new QServerConnect(a, false, new QServerConnect.AsyncResponse() {

                                @Override
                                public void processFinish(String[] output) {
                                    Log.d("ServerResponse", output[0]);
                                    //Here you will receive the result fired from async class
                                    //of onPostExecute(result) method.
                                    if (output[0].equalsIgnoreCase("failed")) {
                                        Toast.makeText(getBaseContext(), "Network Error", Toast.LENGTH_SHORT).show();
                                    } else {
                                        try {
                                            JSONObject response_json = new JSONObject(output[0]);
                                            String qresponse = response_json.getString("qresponse");

                                            if (qresponse.equalsIgnoreCase("Full")) {
                                                new AlertDialog.Builder(a)
                                                    .setTitle("Device Full")
                                                    .setMessage("The maximum number of notifications has been reached for this device. Some notifications must be deleted before more can be added.")
                                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .show();
                                            } else if (qresponse.equalsIgnoreCase("Success")) {
                                                Toast.makeText(getBaseContext(), "Notification Successfully Added", Toast.LENGTH_SHORT).show();

                                                Integer num_notifs = aqua_shared_prefs.getInt("num_notifs", -1);

                                                if (num_notifs < 0) {
                                                    Toast.makeText(getBaseContext(), "Notification Error 101", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    num_notifs++;

                                                    aqua_shared_prefs.edit().putString("!ntf_" + String.valueOf(num_notifs) + "_tkey", aquakey).apply();
                                                    aqua_shared_prefs.edit().putString("!ntf_" + String.valueOf(num_notifs) + "_uuid", uuid).apply();
                                                    aqua_shared_prefs.edit().putString("!ntf_" + String.valueOf(num_notifs) + "_data", notif_data.toString()).apply();
                                                    aqua_shared_prefs.edit().putInt("num_notifs", num_notifs).apply();

                                                    finish();
                                                }
                                            } else {
                                                Toast.makeText(getBaseContext(), "Unknown Failure 202", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).execute(outgoing_json);

                        } catch (JSONException e) {
                            Toast.makeText(getBaseContext(), "102: Device data corrupted", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

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

        btn_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNotif.this, AddNotifSelectAlarm.class);
                startActivityForResult(intent, GET_ALARM_FOR_NOTIF);
            }
        });

    }

    private String getNewNotifSentenceAndUpdateServerVars() {
        String dev_buf;
        String trig_buf;
        String alm_buf;
        String trg_buf;

        String cont_geo = "is inside ";

        String start = "Whenever ";

        if (trig.isEmpty()) triggerType = "Unk";
        else if (trig.startsWith("low")) {
            if (continuous) start = "Whenever ";
            triggerType = "lowBattery";
        } else if (trig.startsWith("enter")) {
            triggerType = "entersGeo";
            String[] chop = trig.split(" ");
            geo = chop[1].substring(1, chop[1].length() - 1);
        } else if (trig.startsWith("exit")) {
            triggerType = "exitsGeo";
            String[] chop = trig.split(" ");
            geo = chop[1].substring(1, chop[1].length() - 1);
        } else if (trig.equalsIgnoreCase("uploads data")) {
            triggerType = "uploadsData";
        } else if (trig.equalsIgnoreCase("starts moving")) {
            triggerType = "startsMoving";
        } else if (trig.equalsIgnoreCase("stops moving")) {
            triggerType = "stopsMoving";
        } else if (trig.startsWith("sees")) {
            triggerType = "seesMac";
            String[] chop = trig.split(" ");
            mac = chop[1];
        } else triggerType = "Error";

        if (dev.isEmpty()) dev_buf = "<?>";
            else dev_buf = dev;
        if (trig.isEmpty()) trig_buf = " <?>";
        else if (trig.equalsIgnoreCase("low battery")) {
            if (continuous) trig_buf = "'s battery is low";
            else trig_buf = "'s battery changes to low";
        } else if (trig.startsWith("enter")) {
            if (continuous) trig_buf = " is inside \"" + geo + "\"";
            else trig_buf = " " + trig;
        } else if (trig.startsWith("exit")) {
            if (continuous) trig_buf = " is outside \"" + geo + "\"";
            else trig_buf = " " + trig;
        } else if (trig.startsWith("sees")) {
            if (continuous) {
                trig_buf = " can see " + mac;
            }
            else {
                start = "When ";
                trig_buf = " gains sight of " + mac;
            }
        } else if (trig.startsWith("starts")) {
            if (continuous) trig_buf = " moves";
            else {
                trig_buf = " begins moving";
            }
        } else if (trig.startsWith("stops")) {
            if (continuous) trig_buf = " is stopped";
            else {
                trig_buf = " stops";
            }
        }
        else trig_buf = " " + trig;
        if (alm.isEmpty()) alm_buf = "a <?>";
            else if (alm.equalsIgnoreCase("text")) alm_buf = "a text.";
            else alm_buf = "an e-mail.";
        if (trg.isEmpty()) trg_buf = "<?>";
            else trg_buf = trg;

        String full_sentence = start + dev_buf + trig_buf + ", send " + trg_buf + " " + alm_buf;

        return full_sentence;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_DEVICE_FOR_NOTIF) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                String htmltext = "<font color=#57a6e2>Device: </font> <font color=#ffffff>" + result + "</font>";
                tv_device.setText(Html.fromHtml(htmltext));
                dev = result;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        } else if (requestCode == GET_TRIGGER_FOR_NOTIF) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                String htmltext = "<font color=#57a6e2>Trigger: </font> <font color=#ffffff>" + result + "</font>";
                tv_trigger.setText(Html.fromHtml(htmltext));
                trig = result;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        } else if (requestCode == GET_ALARM_FOR_NOTIF) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                String target = data.getStringExtra("target");
                String htmltext = "<font color=#57a6e2>Alarm: </font> <font color=#ffffff>" + result + "</font>";
                tv_alarm.setText(Html.fromHtml(htmltext));
                alm = result;
                trg = target;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }

        notif_sentence.setText(getNewNotifSentenceAndUpdateServerVars());

    }
}
