package com.example.tars.aquamanager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Launcher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        if (!aqua_shared_prefs.getBoolean("!set_showLauncher", true)) {
            Intent intent=new Intent(Launcher.this, Main.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Button btn_start = (Button) findViewById(R.id.button);
        final RelativeLayout btn_color = (RelativeLayout) findViewById(R.id.rl);

        AquaUtil.initialSharedPrefSetup(this);

        btn_start.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn_color.setBackgroundColor(ContextCompat.getColor(Launcher.this, R.color.holoBlueLit));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn_color.setBackgroundColor(ContextCompat.getColor(Launcher.this, R.color.holoBlue));
                    Intent intent=new Intent(Launcher.this, Main.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    }
                return true;
            }
        });
    }
}
