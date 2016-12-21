package com.example.tars.aquamanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class AddDevice extends Activity implements QServerConnect.AsyncResponse{

    private Activity a = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        //((TextView)((LinearLayout)((ViewGroup) getWindow().getDecorView()).getChildAt(0)).getChildAt(0)).setGravity(Gravity.CENTER);

        Button submitButton = (Button) findViewById(R.id.submit_button);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        final String iid = aqua_shared_prefs.getString("iid", "Not Found");
        //Log.d("iid", iid);

        if (submitButton != null) {
            submitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText aquaid_edit = (EditText) findViewById(R.id.aquaid);
                    EditText passcode_edit = (EditText) findViewById(R.id.passcode);

                    final String user_entered_aquaid = aquaid_edit.getText().toString();
                    final String user_entered_passcode = passcode_edit.getText().toString();

                    java.util.Map<String, ?> keys = aqua_shared_prefs.getAll();

                    for (java.util.Map.Entry<String, ?> entry : keys.entrySet()) {
                        if (entry.getKey().endsWith("qdata")) {
                            try {
                                Log.d("test", entry.getValue().toString());
                                JSONObject qdata_obj = new JSONObject(entry.getValue().toString());
                                String already_added_aquaid = qdata_obj.getString("aquaid");
                                String already_added_pass = qdata_obj.getString("pass");

                                Log.d("test", qdata_obj.toString());

                                if (user_entered_aquaid.equalsIgnoreCase(already_added_aquaid) && (user_entered_passcode.equalsIgnoreCase(already_added_pass))) {
                                    Toast.makeText(getBaseContext(), "Device already added", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getBaseContext(), "Bad SharedPrefs", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                    final JSONObject outgoing_json = new JSONObject();

                    try {
                        outgoing_json.put("reqtype", "auth");
                        outgoing_json.put("id", user_entered_aquaid);
                        outgoing_json.put("pass", user_entered_passcode);
                        outgoing_json.put("iid", iid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {

                        new QServerConnect(a, new QServerConnect.AsyncResponse() {

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
                                            Toast.makeText(getBaseContext(), "Authentication Successful", Toast.LENGTH_SHORT).show();

                                            JSONObject dummy = response_json.getJSONObject("qdata");
                                            dummy.put("pass", user_entered_passcode);
                                            dummy.put("aquaid", user_entered_aquaid);

                                            Log.d("dcdq", dummy.toString());

                                            String aqsens = response_json.getString("aqsens");

                                            String qdata = response_json.getString("qdata");

                                            Log.d("dcd", aqsens);

                                            Intent intent = new Intent(a, NewDevice.class);
                                            intent.putExtra("qdata", qdata);
                                            intent.putExtra("aqsens", aqsens);
                                            Log.d("test2", output[1]);
                                            intent.putExtra("loc", output[1]);
                                            intent.putExtra("batt", output[2]);

                                            finish();

                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(getBaseContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).execute(outgoing_json);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            cancelButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
    public void processFinish(String[] output){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        //Log.d("ServerResponse", output);
    }
}

