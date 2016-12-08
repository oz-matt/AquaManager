package com.example.tars.aquamanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.Toast;

public class HomeGeofences extends Fragment {

    public static View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home_geofences, container, false);

        ImageButton add_geo = (ImageButton) view.findViewById(R.id.add_geofence_ibtn);

        add_geo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), GeofenceMap.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh_geofence_table(getContext(), view);
    }

    public static void refresh_geofence_table(Context context, View view) {
        TableLayout geo_table = (TableLayout) view.findViewById(R.id.geo_tl);
        geo_table.removeAllViews();
        if (!AquaUtil.populateGeoRows(context, geo_table, "Map")) Log.d("og", "og");//Toast.makeText(context, "Parse Error", Toast.LENGTH_SHORT).show();
    }
}
