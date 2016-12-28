package com.example.tars.aquamanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Map;

public class RemoveNotifFromID extends Activity {

    private Activity a = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_notif_from_id);

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        Button btn_ok = (Button) findViewById(R.id.ok_btn_rmntf);
        Button btn_cancel = (Button) findViewById(R.id.cancel_btn_rmntf);

        final EditText et_id = (EditText) findViewById(R.id.enter_id);
        final EditText et_name = (EditText) findViewById(R.id.enter_dev);

        et_id.setHint("Notification ID");
        et_name.setHint("Device Name");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_id.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Enter Notification ID", Toast.LENGTH_SHORT).show();
                } else if(et_name.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "Enter Name", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String iid = aqua_shared_prefs.getString("iid", "No IID found");
                        String ntfid = et_id.getText().toString();
                        String aquaname = et_name.getText().toString();

                        JSONObject outgoing_json = new JSONObject();

                        outgoing_json.put("reqtype", "rmntfid");
                        outgoing_json.put("iid", iid);
                        outgoing_json.put("ntfid", ntfid);
                        outgoing_json.put("aquaname", aquaname);

                        new QServerConnect(a, false, new QServerConnect.AsyncResponse() {

                            @Override
                            public void processFinish(String[] output) {
                                //Here you will receive the result fired from async class
                                //of onPostExecute(result) method.
                                if (output[0].equalsIgnoreCase("failed")) {
                                    Toast.makeText(getBaseContext(), "Network Error", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("ServerResponse", output[0]);
                                    try {
                                        JSONObject response_json = new JSONObject(output[0]);

                                        String qresponse = response_json.getString("qresponse");

                                        if (qresponse.equalsIgnoreCase("success")) {

                                            Toast.makeText(getBaseContext(), "Notification Successfully Removed", Toast.LENGTH_SHORT).show();

                                            finish();

                                        } else {
                                            Toast.makeText(getBaseContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getBaseContext(), "JSON Exception 102", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }).execute(outgoing_json);

                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(), "JSON Exception 103", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}