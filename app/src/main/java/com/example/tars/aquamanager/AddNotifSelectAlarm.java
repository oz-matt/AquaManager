package com.example.tars.aquamanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddNotifSelectAlarm extends Activity {

    Context context;

    private int current_checked_alarm = 0;

    static final int TEXT_MSG = 1;
    static final int EMAIL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notif_select_alarm);

        context = this;

        final CheckBox text_cb = (CheckBox) findViewById(R.id.alarm_checkBox_1);
        final CheckBox email_cb = (CheckBox) findViewById(R.id.alarm_checkBox_2);

        Button ok_btn = (Button) findViewById(R.id.ok_btn_alarm);
        Button cancel_btn = (Button) findViewById(R.id.cancel_btn_alarm);

        text_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    email_cb.setChecked(false);
                    current_checked_alarm = TEXT_MSG;
                }
            }
        });

        email_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    text_cb.setChecked(false);
                    current_checked_alarm = EMAIL;
                }
            }
        });


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_checked_alarm == 0) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                } else {

                    switch (current_checked_alarm) {
                        case TEXT_MSG:

                            final EditText input = new EditText(AddNotifSelectAlarm.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            input.setInputType(InputType.TYPE_CLASS_NUMBER);
                            input.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

                            new AlertDialog.Builder(context)
                                    .setTitle("Enter Target")
                                    .setMessage("Please enter the phone number that you want to text when the device is triggered.")
                                    .setView(input)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent returnIntentTxt = new Intent();
                                            returnIntentTxt.putExtra("result", "text");
                                            returnIntentTxt.putExtra("target", input.getText().toString());
                                            setResult(Activity.RESULT_OK, returnIntentTxt);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                            break;
                        case EMAIL:
                            final EditText input_email = new EditText(AddNotifSelectAlarm.this);
                            LinearLayout.LayoutParams lpe = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input_email.setLayoutParams(lpe);

                            new AlertDialog.Builder(context)
                                    .setTitle("Enter Target")
                                    .setMessage("Please enter the address that you want to e-mail when the device is triggered.")
                                    .setView(input_email)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (isEmailValid(input_email.getText().toString())) {
                                                Intent returnIntentTxt = new Intent();
                                                returnIntentTxt.putExtra("result", "e-mail");
                                                returnIntentTxt.putExtra("target", input_email.getText().toString());
                                                setResult(Activity.RESULT_OK, returnIntentTxt);
                                                finish();
                                            } else {
                                                Toast.makeText(context, "Invalid e-mail address", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent returnIntent = new Intent();
                                            setResult(Activity.RESULT_CANCELED, returnIntent);
                                            finish();
                                        }
                                    })
                                    .show();
                            break;
                    }
                }
            }
        });

    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
