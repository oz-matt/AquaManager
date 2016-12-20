package com.example.tars.aquamanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNotifSelectMac extends Activity {

    Context context;

    EditText macfield1;
    EditText macfield2;
    EditText macfield3;
    EditText macfield4;
    EditText macfield5;
    EditText macfield6;

    String mac_formatted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notif_select_mac);

        context = this;

        macfield1 = (EditText) findViewById(R.id.edit_mac_byte1);
        macfield2 = (EditText) findViewById(R.id.edit_mac_byte2);
        macfield3 = (EditText) findViewById(R.id.edit_mac_byte3);
        macfield4 = (EditText) findViewById(R.id.edit_mac_byte4);
        macfield5 = (EditText) findViewById(R.id.edit_mac_byte5);
        macfield6 = (EditText) findViewById(R.id.edit_mac_byte6);

        Button ok_btn = (Button) findViewById(R.id.ok_btn_sel_mac);
        Button cancel_btn = (Button) findViewById(R.id.cancel_btn_sel_mac);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (macAddressIsCorrectFormat()) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", "sees " + mac_formatted);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(context, "Bad MAC Address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        macfield1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (macfield1.getText().toString().length() == 2) macfield2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        macfield2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (macfield2.getText().toString().length() == 2) macfield3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        macfield3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (macfield3.getText().toString().length() == 2) macfield4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        macfield4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (macfield4.getText().toString().length() == 2) macfield5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        macfield5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (macfield5.getText().toString().length() == 2) macfield6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean macAddressIsCorrectFormat () {
        String text1 = macfield1.getText().toString();
        String text2 = macfield2.getText().toString();
        String text3 = macfield3.getText().toString();
        String text4 = macfield4.getText().toString();
        String text5 = macfield5.getText().toString();
        String text6 = macfield6.getText().toString();

        try
        {
            Integer.parseInt(text1,16);
            Integer.parseInt(text2,16);
            Integer.parseInt(text3,16);
            Integer.parseInt(text4,16);
            Integer.parseInt(text5,16);
            Integer.parseInt(text6,16);
        }
        catch (Exception e)
        {
            return false;
        }

        String cat = text1 + ":" + text2 + ":" + text3 + ":" + text4 + ":" + text5 + ":" + text6;

        if (cat.length() == 17) {
            mac_formatted = cat;
            return true;
        }

        return false;
    }
}
