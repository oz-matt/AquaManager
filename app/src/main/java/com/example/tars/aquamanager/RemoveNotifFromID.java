package com.example.tars.aquamanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class RemoveNotifFromID extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_notif_from_id);

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        Button btn_ok = (Button) findViewById(R.id.ok_btn_rmntf);
        Button btn_cancel = (Button) findViewById(R.id.cancel_btn_rmntf);

        final EditText et_id = (EditText) findViewById(R.id.enter_id);
        final EditText et_name = (EditText) findViewById(R.id.enter_dev);

        et_id.setHint("MyID");
        et_name.setHint("MyAqua");

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

                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getBaseContext(), "JSON Exception", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}