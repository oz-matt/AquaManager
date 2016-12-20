package com.example.tars.aquamanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Map;

//import java.util.MainMap;

public class Main extends AppCompatActivity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getSupportActionBar().setElevation(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.devices));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.notifications96));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.fence));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        context = this;

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.item_refresh:
                updateEveryDevicesAqsensElementAndRefreshTable(this);
                break;
            case R.id.item_settings:
                Toast.makeText(getBaseContext(), "Settings", Toast.LENGTH_SHORT).show();
                Intent open_settings = new Intent(getApplicationContext(), Settings.class);
                startActivity(open_settings);
                break;
            case R.id.item_map:
                Toast.makeText(getBaseContext(), "MainMap", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Main.this, MainMap.class);
                intent.putExtra("populate_extra", "all");
                startActivity(intent);
                //startActivity(new Intent(this, MainMap.class));
                break;
        }
        return true;
    }

    private static boolean checkIfAllGeofencesHaveGoodLocationsAndFixTheOnesThatDont() {

        /*final SharedPreferences aqua_shared_prefs = context.getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        Map<String,?> keys = aqua_shared_prefs.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("!geo_") && key.endsWith("_settings")) {
                try {
                    JSONObject geo_settings = new JSONObject(entry.getValue().toString());
                    String locstr = geo_settings.getString("location");
                    if (locstr.equalsIgnoreCase("<???>")) {
                        //Found a geofence with no location


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/

        //ToDo: Implement later :[

        return true;
    }

    public static void updateEveryDevicesAqsensElementAndRefreshTable(final Context context) {

        String[] dummy = {};

        try {

            new QServerGetAqsens((Activity) context, new QServerGetAqsens.AsyncResponse(){

                @Override
                public void processFinish(String output){
                    if (output.equalsIgnoreCase("cxn error")) {
                        Toast.makeText(context, "Connection Error", Toast.LENGTH_SHORT).show();
                    } else if (output.equalsIgnoreCase("no devs")) {
                        Toast.makeText(context, "No Devices Found", Toast.LENGTH_SHORT).show();
                    } else if (output.equalsIgnoreCase("success")) {
                        HomeDevices.refresh_device_table(context, HomeDevices.view);

                        if (checkIfAllGeofencesHaveGoodLocationsAndFixTheOnesThatDont()) {

                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to refresh geos", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute(dummy);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "QServer Error", Toast.LENGTH_SHORT).show();
        }

    }

    public void onListItemClick(ListView parent, View v, int position, long id){
        Intent intent = new Intent(Main.this,MainMap.class);
        Bundle bundle = new Bundle();
        //bundle.putString("string", strings[position]);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
