package com.example.tars.aquamanager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class HomeNotifications extends Fragment {

    public static View view;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_notifications, container, false);
        context = this.getContext();

        ImageButton add_notif = (ImageButton) view.findViewById(R.id.add_notif_ibtn);

        add_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (doesUserHaveAtLeastOneDevice()) {
                    Intent intent=new Intent(getContext(), AddNotif.class);
                    startActivity(intent);
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle("Need Device")
                            .setMessage("You must add a device before creating a notification.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
        return view;
    }

    private boolean doesUserHaveAtLeastOneDevice() {
        final SharedPreferences aqua_shared_prefs = context.getSharedPreferences("aqua_shared_prefs", context.MODE_PRIVATE);
        java.util.Map<String,?> keys = aqua_shared_prefs.getAll();

        for(java.util.Map.Entry<String,?> entry : keys.entrySet()) {
            if (entry.getKey().startsWith("!dev")) {
                return true;
            }
        }
        return false;
    }
}