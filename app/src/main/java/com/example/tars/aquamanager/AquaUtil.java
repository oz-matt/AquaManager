package com.example.tars.aquamanager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by TARS on 6/6/2016.
 *
 *

 Shared Preferences Layout

 SharedPreferences "aqua_shared_prefs"
         iid
         next_device_id
         !dev_MyAqua_qdata
             aquaid
             aquakey
             passcode
             phonenumber
         !dev_MyAqua_aqsens
             [
                0. datetime
                uuid...
                1. datetime
                uuid...
             ]
         !dev_MyAqua_notifs
             [
                 0. datetime
                 uuid...
                 1. datetime
                 uuid...
             ]
        !dev_MyAqua_locstr
            New Fairfield, CT
        !dev_MyAqua_locstrlong
            Musket Ridge Rd, New Fairfield, CT
        !dev_MyAqua_pctbat
            85
        !dev_MyAqua_settings
            DisplayFlag //Does this module show up with markers when the map button is pressed?
            MarkerColor
            MaxMarkersToDisplay

        !dev_MyAqua2_qdata.............



        **Geofences**


        !geo_MyGeo_settings -- IF GEOTYPE = CIRCLE
            location
            area
            geotype
            geodata
            lat
            lon

         !geo_MyGeo_settings -- IF GEOTYPE = POLYGON
             location
             area
             geotype
             geodata = JSON Array of JSON Objects

        **Notifications**

        !ntf_[num_notifs]_tkey
            [aquakey]
        !ntf_[num_notifs]_uuid
            [uuid]
        !ntf_[num_notifs]_data
            alert: text, email
            aquaname: myaqua
            ntfuuid: 0e4fhtvnuh-345356-5646-86576-t7vnyfvuh
            target: +12037701412
            trigger: entersGeo
            geotype: polygon
            geoname: mygeo
            geodata:
                [
                    {
                        lat: 56.6877667
                        lon: 123.747676
                    },
                    {
                        lat: 56.6877667
                        lon: 123.747676
                    }
                    ...
                 ]


 *
 */
public class AquaUtil {

    //public static int check=0;
    public static int checkgeo=0;

    public static void initialSharedPrefSetup(Context context) {

        SharedPreferences aqua_shared_prefs = context.getSharedPreferences("aqua_shared_prefs", context.MODE_PRIVATE);

        aqua_shared_prefs.edit().clear().commit();

        aqua_shared_prefs.edit().putInt("!set_mapDisplayType", 0).apply();
        aqua_shared_prefs.edit().putString("!set_numberOfMarkersToShow", "50").apply();
        aqua_shared_prefs.edit().putBoolean("!set_showGeos", true).apply();

        if (!aqua_shared_prefs.contains("iid")) {
            String iid = UUID.randomUUID().toString();
            aqua_shared_prefs.edit().putString("iid", iid).commit();
        }

        if (!aqua_shared_prefs.contains("num_notifs")) {
            aqua_shared_prefs.edit().putInt("num_notifs", 0).commit();
        }

    }

    public static boolean populateDeviceRows(final Context context, TableLayout layout, final String clickFunction) {

        final SharedPreferences aqua_shared_prefs = context.getSharedPreferences("aqua_shared_prefs", context.MODE_PRIVATE);
        java.util.Map<String,?> keys = aqua_shared_prefs.getAll();

        int margin = (int) context.getResources().getDisplayMetrics().density;

        for(java.util.Map.Entry<String,?> entry : keys.entrySet()) {
            Log.d("cdc", "key:" + entry.getKey() + ", val:" + entry.getValue().toString());
            if (entry.getKey().endsWith("qdata")) {
                String key = entry.getKey();
                String val = entry.getValue().toString();
                final String name = key.substring(5, key.length() - 6);

                String aqsens_str = aqua_shared_prefs.getString("!dev_" + name + "_aqsens", "Not found");
                Log.d("cdc", aqsens_str);

                try {
                    JSONArray aqsens_obj = new JSONArray(aqsens_str);

                    int device_row_color;

                    Log.d("calc", aqsens_obj.toString());

                    device_row_color = calcDeviceColor(context, aqsens_obj);

                    if (device_row_color == 0) return false;

                    final int final_row_color = device_row_color;

                    final ImageButton device_settings = new ImageButton(context);
                    device_settings.setImageResource(R.drawable.settings);
                    device_settings.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    device_settings.setBackgroundColor(device_row_color);
                    device_settings.setPadding(0,0,1,0);
                    RelativeLayout.LayoutParams nilp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    nilp.width = (int) convertDpToPixel(32, context);
                    nilp.height = (int) convertDpToPixel(32, context);
                    nilp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    //nilp.setMargins(0,margin,margin,margin);
                    device_settings.setLayoutParams(nilp);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        // If we're running on Honeycomb or newer, then we can use the Theme's
                        // selectableItemBackground to ensure that the View has a pressed state
                        TypedValue outValue = new TypedValue();
                        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                        device_settings.setBackgroundResource(outValue.resourceId);
                    }

                    //device_settings.setClickable(true);

                    final RelativeLayout icon_rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams icon_rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //icon_rl_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);'
                    //icon_rl_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    icon_rl.setLayoutParams(icon_rl_lp);
                    icon_rl_lp.setMargins(0,margin,margin,margin);
                    //icon_rl.setId(R.id.settings_icon_id);
                    icon_rl.setBackgroundColor(device_row_color);
                    icon_rl.addView(device_settings);
                    //icon_rl.setPadding(0,0,30,0);
                    //icon_rl.setId(R.id.icon_rl_id);

                    String[] strings={"Info","Raw Data","Remove"};
                    final NDSpinner spinner=new NDSpinner(context);
                    spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,strings));

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        private int check = 0;

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            check=check+1;
                            if(check > 1) {
                                String selectedItem = parent.getItemAtPosition(position).toString();
                                if (selectedItem.equals("Info")) {
                                    Intent intent = new Intent(Main.context, DeviceSettings.class);
                                    intent.putExtra("name", name);
                                    context.startActivity(intent);
                                } else if (selectedItem.equals("Raw Data")) {
                                    Intent intent = new Intent(Main.context, DeviceRawData.class);
                                    intent.putExtra("name", name);
                                    context.startActivity(intent);
                                } else if (selectedItem.equals("Remove")) {
                                    new AlertDialog.Builder(context)
                                            .setTitle("Remove entry")
                                            .setMessage("Are you sure you want to remove this entry, along with all associated notifications?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteAqua(name, aqua_shared_prefs);
                                                    HomeDevices.refresh_device_table(context, HomeDevices.view);
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                } else {
                                    Toast.makeText(context, "??", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            Toast.makeText(context, "nothing", Toast.LENGTH_SHORT).show();
                        }
                    });

                    icon_rl.addView(spinner);
                    spinner.setVisibility(Spinner.INVISIBLE);

                    final RelativeLayout icon_rl_rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams icon_rl_rl_lp = new RelativeLayout.LayoutParams((int) convertDpToPixel(57, context), (int) convertDpToPixel(72, context));
                    icon_rl_rl_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    icon_rl_rl.setLayoutParams(icon_rl_rl_lp);
                    icon_rl_rl.setId(R.id.settings_icon_id);
                    icon_rl_rl.setBackgroundColor(context.getResources().getColor(R.color.holoBlueMidnight));
                    icon_rl_rl.addView(icon_rl);


                    device_settings.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                icon_rl.setBackgroundColor(lighter(final_row_color,(float)0.14));

                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                icon_rl.setBackgroundColor(final_row_color);
                                spinner.performClick();
                            }
                            return true;
                        }
                    });

                    final TextView newName = new TextView(context);
                    TableRow.LayoutParams nnlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .24f);
                    nnlp.setMargins(margin,margin,0,margin);
                    newName.setLayoutParams(nnlp);
                    nnlp.width = (0);
                    newName.setLines(2);
                    newName.setGravity(Gravity.CENTER);
                    newName.setBackgroundColor(device_row_color);
                    newName.setText(name);
                    newName.setTextColor(Color.WHITE);

                    final TextView newLoc = new TextView(context);
                    TableRow.LayoutParams nllp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .565f);
                    nllp.setMargins(0, margin, 0, margin);
                    newLoc.setLayoutParams(nllp);
                    newLoc.setText(aqua_shared_prefs.getString("!dev_" + name + "_locstr", "Not Found"));
                    nllp.width = (0);
                    newLoc.setLines(2);
                    newLoc.setGravity(Gravity.CENTER);
                    newLoc.setBackgroundColor(device_row_color);
                    newLoc.setTextColor(Color.WHITE);

                    final TextView newChg = new TextView(context);
                    TableRow.LayoutParams nclp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .195f);
                    nclp.width=(0);
                    newChg.setGravity(Gravity.CENTER);
                    nclp.setMargins(0, margin, 0, margin);
                    newChg.setText(aqua_shared_prefs.getString("!dev_" + name + "_pctbat", "N/F"));
                    newChg.setLines(2);
                    newChg.setLayoutParams(nclp);
                    newChg.setBackgroundColor(device_row_color);
                    newChg.setTextColor(Color.WHITE);

                    TableRow newModuleRow = new TableRow(context);
                    LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    newModuleRow.addView(newName,nnlp);
                    newModuleRow.addView(newLoc, nllp);
                    newModuleRow.addView(newChg, nclp);
                    newModuleRow.setLayoutParams(lllp);
                    newModuleRow.setBackgroundColor(context.getResources().getColor(R.color.holoBlueMidnight));


                    final LinearLayout header_ll = new LinearLayout(context);
                    RelativeLayout.LayoutParams header_ll_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(72, context));
                    header_ll_lp.addRule(RelativeLayout.LEFT_OF, R.id.settings_icon_id);
                    header_ll.setLayoutParams(header_ll_lp);
                    header_ll.setBackgroundColor(device_row_color);
                    header_ll.addView(newModuleRow);
                    header_ll.setClickable(true);

                    ViewGroup parentView = header_ll;
                    parentView.bringToFront();

                    RelativeLayout row_rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams row_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(72, context));
                    row_rl.setLayoutParams(row_lp);
                    row_rl.addView(icon_rl_rl);
                    row_rl.addView(header_ll);

                    newLoc.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                newLoc.setBackgroundColor(lighter(final_row_color,(float)0.14));
                                newChg.setBackgroundColor(lighter(final_row_color,(float)0.14));
                                newName.setBackgroundColor(lighter(final_row_color,(float)0.14));
                                icon_rl.setBackgroundColor(lighter(final_row_color,(float)0.14));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                newLoc.setBackgroundColor(final_row_color);
                                newChg.setBackgroundColor(final_row_color);
                                newName.setBackgroundColor(final_row_color);
                                icon_rl.setBackgroundColor(final_row_color);
                                if (clickFunction.equalsIgnoreCase("None")) Toast.makeText(context, "Eye Of Horus", Toast.LENGTH_SHORT).show();
                                if (clickFunction.equalsIgnoreCase("MainMap")) {
                                    Intent intent=new Intent(context, MainMap.class);
                                    intent.putExtra("populate_extra", "!dev_" + name);
                                    context.startActivity(intent);
                                }
                            }
                            return true;
                        }
                    });

                    newChg.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                newLoc.setBackgroundColor(lighter(final_row_color,(float)0.14));
                                newChg.setBackgroundColor(lighter(final_row_color,(float)0.14));
                                newName.setBackgroundColor(lighter(final_row_color,(float)0.14));
                                icon_rl.setBackgroundColor(lighter(final_row_color,(float)0.14));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                newLoc.setBackgroundColor(final_row_color);
                                newChg.setBackgroundColor(final_row_color);
                                newName.setBackgroundColor(final_row_color);
                                icon_rl.setBackgroundColor(final_row_color);
                            }
                            return true;
                        }
                    });

                    newName.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                newLoc.setBackgroundColor(lighter(final_row_color,(float)0.14));
                                newChg.setBackgroundColor(lighter(final_row_color,(float)0.14));
                                newName.setBackgroundColor(lighter(final_row_color,(float)0.14));
                                icon_rl.setBackgroundColor(lighter(final_row_color,(float)0.14));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                newLoc.setBackgroundColor(final_row_color);
                                newChg.setBackgroundColor(final_row_color);
                                newName.setBackgroundColor(final_row_color);
                                icon_rl.setBackgroundColor(final_row_color);
                            }
                            return true;
                        }
                    });

                    layout.addView(row_rl, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }

    public static void populateNotificationRows(final Context context, TableLayout layout, final String clickFunction) {
        final SharedPreferences aqua_shared_prefs = context.getSharedPreferences("aqua_shared_prefs", context.MODE_PRIVATE);
        java.util.Map<String,?> keys = aqua_shared_prefs.getAll();

        int margin = (int) context.getResources().getDisplayMetrics().density;

        for(java.util.Map.Entry<String,?> entry : keys.entrySet()) {
            if (entry.getKey().endsWith("tkey")) {
                String key = entry.getKey();
                String val = entry.getValue().toString();
                final String ntf_num = key.substring(5, key.length() - 5);

                String ntf_data_str = aqua_shared_prefs.getString("!ntf_" + ntf_num + "_data", "Not Found");

                try {
                    JSONObject ntf_data = new JSONObject(ntf_data_str);

                    String ntf_device_name = ntf_data.getString("aquaname");
                    String ntf_trigger = ntf_data.getString("trigger");
                    String ntf_alert = ntf_data.getString("alert");
                    String ntf_target = ntf_data.getString("target");

                    final int device_row_color = ContextCompat.getColor(context, R.color.holoBlueLit);

                    final ImageButton device_settings = new ImageButton(context);
                    device_settings.setImageResource(R.drawable.settings);
                    device_settings.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    device_settings.setBackgroundColor(device_row_color);
                    device_settings.setPadding(0,0,1,0);
                    RelativeLayout.LayoutParams nilp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    nilp.width = (int) convertDpToPixel(32, context);
                    nilp.height = (int) convertDpToPixel(32, context);
                    nilp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    //nilp.setMargins(0,margin,margin,margin);
                    device_settings.setLayoutParams(nilp);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        // If we're running on Honeycomb or newer, then we can use the Theme's
                        // selectableItemBackground to ensure that the View has a pressed state
                        TypedValue outValue = new TypedValue();
                        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                        device_settings.setBackgroundResource(outValue.resourceId);
                    }

                    //device_settings.setClickable(true);

                    final RelativeLayout icon_rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams icon_rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //icon_rl_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);'
                    //icon_rl_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    icon_rl.setLayoutParams(icon_rl_lp);
                    icon_rl_lp.setMargins(0,margin,margin,margin);
                    //icon_rl.setId(R.id.settings_icon_id);
                    icon_rl.setBackgroundColor(device_row_color);
                    icon_rl.addView(device_settings);
                    //icon_rl.setPadding(0,0,30,0);
                    //icon_rl.setId(R.id.icon_rl_id);

                    String[] strings={"Settings","Remove"};
                    final NDSpinner spinner=new NDSpinner(context);
                    spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,strings));

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        private int check = 0;

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            check=check+1;
                            if(check > 1) {
                                String selectedItem = parent.getItemAtPosition(position).toString();
                                if (selectedItem.equals("Settings")) {
                                    Intent intent=new Intent(Main.context, NotifSettings.class);
                                    intent.putExtra("ntf_num", ntf_num);
                                    context.startActivity(intent);
                                } else if (selectedItem.equals("Remove")) {
                                    new AlertDialog.Builder(context)
                                            .setTitle("Remove entry")
                                            .setMessage("Are you sure you want to remove this notification?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteNotif(ntf_num, aqua_shared_prefs, context);
                                                    //deleteAqua(name, aqua_shared_prefs);
                                                    //HomeDevices.refresh_device_table(context, HomeDevices.view);
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                } else {
                                    Toast.makeText(context, "??", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            Toast.makeText(context, "nothing", Toast.LENGTH_SHORT).show();
                        }
                    });

                    icon_rl.addView(spinner);
                    spinner.setVisibility(Spinner.INVISIBLE);

                    final RelativeLayout icon_rl_rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams icon_rl_rl_lp = new RelativeLayout.LayoutParams((int) convertDpToPixel(57, context), (int) convertDpToPixel(72, context));
                    icon_rl_rl_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    icon_rl_rl.setLayoutParams(icon_rl_rl_lp);
                    icon_rl_rl.setId(R.id.settings_icon_id);
                    icon_rl_rl.setBackgroundColor(context.getResources().getColor(R.color.holoBlueMidnight));
                    icon_rl_rl.addView(icon_rl);


                    device_settings.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                icon_rl.setBackgroundColor(lighter(device_row_color,(float)0.14));

                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                icon_rl.setBackgroundColor(device_row_color);
                                spinner.performClick();
                            }
                            return true;
                        }
                    });

                    final TextView newName = new TextView(context);
                    TableRow.LayoutParams nnlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .45f);
                    nnlp.setMargins(margin,margin,0,margin);
                    newName.setLayoutParams(nnlp);
                    nnlp.width = (0);
                    newName.setLines(2);
                    newName.setGravity(Gravity.CENTER);
                    newName.setBackgroundColor(device_row_color);
                    newName.setText(ntf_device_name);
                    newName.setTextColor(Color.WHITE);

                    final TextView newLoc = new TextView(context);
                    TableRow.LayoutParams nllp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .355f);
                    nllp.setMargins(0, margin, 0, margin);
                    newLoc.setLayoutParams(nllp);
                    newLoc.setText(ntf_trigger);
                    nllp.width = (0);
                    newLoc.setLines(2);
                    newLoc.setGravity(Gravity.CENTER);
                    newLoc.setBackgroundColor(device_row_color);
                    newLoc.setTextColor(Color.WHITE);

                    final TextView newChg = new TextView(context);
                    TableRow.LayoutParams nclp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .195f);
                    nclp.width=(0);
                    newChg.setGravity(Gravity.CENTER);
                    nclp.setMargins(0, margin, 0, margin);
                    newChg.setText(ntf_alert);
                    newChg.setLines(2);
                    newChg.setLayoutParams(nclp);
                    newChg.setBackgroundColor(device_row_color);
                    newChg.setTextColor(Color.WHITE);

                    TableRow newModuleRow = new TableRow(context);
                    LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    newModuleRow.addView(newName,nnlp);
                    newModuleRow.addView(newLoc, nllp);
                    newModuleRow.addView(newChg, nclp);
                    newModuleRow.setLayoutParams(lllp);
                    newModuleRow.setBackgroundColor(context.getResources().getColor(R.color.holoBlueMidnight));


                    final LinearLayout header_ll = new LinearLayout(context);
                    RelativeLayout.LayoutParams header_ll_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(72, context));
                    header_ll_lp.addRule(RelativeLayout.LEFT_OF, R.id.settings_icon_id);
                    header_ll.setLayoutParams(header_ll_lp);
                    header_ll.setBackgroundColor(device_row_color);
                    header_ll.addView(newModuleRow);
                    header_ll.setClickable(true);

                    ViewGroup parentView = header_ll;
                    parentView.bringToFront();

                    RelativeLayout row_rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams row_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(72, context));
                    row_rl.setLayoutParams(row_lp);
                    row_rl.addView(icon_rl_rl);
                    row_rl.addView(header_ll);

                    newLoc.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                newLoc.setBackgroundColor(lighter(device_row_color,(float)0.14));
                                newChg.setBackgroundColor(lighter(device_row_color,(float)0.14));
                                newName.setBackgroundColor(lighter(device_row_color,(float)0.14));
                                icon_rl.setBackgroundColor(lighter(device_row_color,(float)0.14));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                newLoc.setBackgroundColor(device_row_color);
                                newChg.setBackgroundColor(device_row_color);
                                newName.setBackgroundColor(device_row_color);
                                icon_rl.setBackgroundColor(device_row_color);
                                if (clickFunction.equalsIgnoreCase("None")) Toast.makeText(context, "Eye Of Horus", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });

                    newChg.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                newLoc.setBackgroundColor(lighter(device_row_color,(float)0.14));
                                newChg.setBackgroundColor(lighter(device_row_color,(float)0.14));
                                newName.setBackgroundColor(lighter(device_row_color,(float)0.14));
                                icon_rl.setBackgroundColor(lighter(device_row_color,(float)0.14));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                newLoc.setBackgroundColor(device_row_color);
                                newChg.setBackgroundColor(device_row_color);
                                newName.setBackgroundColor(device_row_color);
                                icon_rl.setBackgroundColor(device_row_color);
                            }
                            return true;
                        }
                    });

                    newName.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                newLoc.setBackgroundColor(lighter(device_row_color,(float)0.14));
                                newChg.setBackgroundColor(lighter(device_row_color,(float)0.14));
                                newName.setBackgroundColor(lighter(device_row_color,(float)0.14));
                                icon_rl.setBackgroundColor(lighter(device_row_color,(float)0.14));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                newLoc.setBackgroundColor(device_row_color);
                                newChg.setBackgroundColor(device_row_color);
                                newName.setBackgroundColor(device_row_color);
                                icon_rl.setBackgroundColor(device_row_color);
                            }
                            return true;
                        }
                    });

                    layout.addView(row_rl, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSON Exception 105", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static int calcDeviceColor(Context context, JSONArray aqsens_array) {
        if (aqsens_array.length() >= 1) {
            try {
                JSONObject latest_element = aqsens_array.getJSONObject(0);
                JSONObject latest_gps_minimum = latest_element.getJSONObject("gpsminimum");
                String latest_datetime = latest_gps_minimum.getString("time");

                Date now = new Date();
                Log.d("now", now.toString());

                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date result1 = df1.parse(latest_datetime);
                Log.d("aqua_pt", result1.toString());

                Long diff = now.getTime() - result1.getTime();

                Log.d("now", diff.toString());

                if (diff > 21600000) {
                    return ContextCompat.getColor(context, R.color.noRecentUpdateRed);
                } else if (diff > 7200000) {
                    return ContextCompat.getColor(context, R.color.semiRecentUpdateOrange);
                } else {
                    return ContextCompat.getColor(context, R.color.recentUpdateGreen);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    private static void deleteAqua(String name_to_delete, SharedPreferences aqua_shared_prefs) {
        SharedPreferences.Editor editor = aqua_shared_prefs.edit();
        editor.remove("!dev_" + name_to_delete + "_qdata");
        editor.remove("!dev_" + name_to_delete + "_aqsens");
        editor.remove("!dev_" + name_to_delete + "_notifs");
        editor.remove("!dev_" + name_to_delete + "_locstr");
        editor.remove("!dev_" + name_to_delete + "_pctbat");
        editor.apply();
    }

    private static void deleteNotif(final String ntf_num, final SharedPreferences aqua_shared_prefs, final Context context) {
        String ntfdata = aqua_shared_prefs.getString("!ntf_" + ntf_num + "_data", "Not found");
        try {
            JSONObject ntf_data = new JSONObject(ntfdata);

            String ntfuuid = ntf_data.getString("ntfuuid");
            String ntfdev = ntf_data.getString("aquaname");
            String iid = aqua_shared_prefs.getString("iid", "Not found");

            JSONObject outgoing_json = new JSONObject();
            outgoing_json.put("reqtype", "rmntfid");
            outgoing_json.put("ntfid", ntfuuid);
            outgoing_json.put("aquaname", ntfdev);
            outgoing_json.put("iid", iid);

            final Activity activity = (Activity) context;

            new QServerConnect(activity, false, new QServerConnect.AsyncResponse() {

                @Override
                public void processFinish(String[] output) {
                    //Here you will receive the result fired from async class
                    //of onPostExecute(result) method.
                    if (output[0].equalsIgnoreCase("failed")) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("ServerResponse", output[0]);
                        try {
                            JSONObject response_json = new JSONObject(output[0]);

                            String qresponse = response_json.getString("qresponse");

                            if (qresponse.equalsIgnoreCase("success")) {
                                Toast.makeText(context, "Notification Successfully Removed", Toast.LENGTH_SHORT).show();

                                aqua_shared_prefs.edit().remove("!ntf_" + ntf_num + "_data").apply();
                                aqua_shared_prefs.edit().remove("!ntf_" + ntf_num + "_uuid").apply();
                                aqua_shared_prefs.edit().remove("!ntf_" + ntf_num + "_tkey").apply();

                                HomeNotifications.refresh_notification_table(context, HomeNotifications.view);
                            } else {
                                Toast.makeText(context, "Failed to Remove Notification", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).execute(outgoing_json);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "JSON Exception 106", Toast.LENGTH_SHORT).show();
        }
    }

    private static void deleteGeo(String name_to_delete, SharedPreferences aqua_shared_prefs) {
        SharedPreferences.Editor editor = aqua_shared_prefs.edit();
        editor.remove("!geo_" + name_to_delete + "_settings");
        editor.apply();
    }

    private static String newNameIsAcceptable(String newname, Context context) {

        if (newname.length() < 2) return "TooShort";

        final SharedPreferences aqua_shared_prefs = context.getSharedPreferences("aqua_shared_prefs", context.MODE_PRIVATE);
        java.util.Map<String,?> keys = aqua_shared_prefs.getAll();

        for(java.util.Map.Entry<String,?> entry : keys.entrySet()) {
            if (entry.getKey().endsWith("qdata")) {
                String key = entry.getKey();
                final String name = key.substring(5, key.length() - 6);

                if (newname.equalsIgnoreCase(name)) return "NameExists";
            }
        }
        return "Good";
    }

    private static String newGeoNameIsAcceptable(String newname, Context context) {

        if (newname.length() < 2) return "TooShort";

        final SharedPreferences aqua_shared_prefs = context.getSharedPreferences("aqua_shared_prefs", context.MODE_PRIVATE);
        java.util.Map<String,?> keys = aqua_shared_prefs.getAll();

        for(java.util.Map.Entry<String,?> entry : keys.entrySet()) {
            if (entry.getKey().startsWith("!geo_")) {
                String key = entry.getKey();
                final String name = key.substring(5, key.length() - 9);

                if (newname.equalsIgnoreCase(name)) return "NameExists";
            }
        }
        return "Good";
    }

    public static boolean populateGeoRows(final Context context, TableLayout layout, final String clickFunction) {
        final SharedPreferences aqua_shared_prefs = context.getSharedPreferences("aqua_shared_prefs", context.MODE_PRIVATE);
        java.util.Map<String,?> keys = aqua_shared_prefs.getAll();

        int margin = (int) context.getResources().getDisplayMetrics().density;

        checkgeo = 0;

        for(java.util.Map.Entry<String,?> entry : keys.entrySet()) {
            Log.d("cdc", "key:" + entry.getKey() + ", val:" + entry.getValue().toString());
            if (entry.getKey().startsWith("!geo_")) {
                String key = entry.getKey();
                String val = entry.getValue().toString();
                final String geofence_name = key.substring(5, key.length() - 9);

                try {
                    JSONObject geo = new JSONObject(val);

                    String geofence_location = geo.getString("location");
                    String geofence_area = geo.getString("area");


                    final ImageButton device_settings = new ImageButton(context);
                    device_settings.setImageResource(R.drawable.settings);
                    device_settings.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    device_settings.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                    device_settings.setPadding(0,0,1,0);
                    RelativeLayout.LayoutParams nilp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    nilp.width = (int) convertDpToPixel(32, context);
                    nilp.height = (int) convertDpToPixel(32, context);
                    nilp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    //nilp.setMargins(0,margin,margin,margin);
                    device_settings.setLayoutParams(nilp);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        // If we're running on Honeycomb or newer, then we can use the Theme's
                        // selectableItemBackground to ensure that the View has a pressed state
                        TypedValue outValue = new TypedValue();
                        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                        device_settings.setBackgroundResource(outValue.resourceId);
                    }

                    //device_settings.setClickable(true);

                    final RelativeLayout icon_rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams icon_rl_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    //icon_rl_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);'
                    //icon_rl_lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    icon_rl.setLayoutParams(icon_rl_lp);
                    icon_rl_lp.setMargins(0,margin,margin,margin);
                    //icon_rl.setId(R.id.settings_icon_id);
                    icon_rl.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                    icon_rl.addView(device_settings);
                    //icon_rl.setPadding(0,0,30,0);
                    //icon_rl.setId(R.id.icon_rl_id);

                    String[] strings={"Remove"};
                    final NDSpinner spinner=new NDSpinner(context);
                    spinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item,strings));

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        private int chexgeo = 0;

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            chexgeo=chexgeo+1;
                            if(chexgeo > 1) {
                                String selectedItem = parent.getItemAtPosition(position).toString();
                                if (selectedItem.equals("Remove")) {
                                    new AlertDialog.Builder(context)
                                            .setTitle("Remove entry")
                                            .setMessage("Are you sure you want to remove this entry, along with all associated notifications?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteGeo(geofence_name, aqua_shared_prefs);
                                                    HomeGeofences.refresh_geofence_table(context, HomeGeofences.view);
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                } else {
                                    Toast.makeText(context, "??", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            Toast.makeText(context, "nothing", Toast.LENGTH_SHORT).show();
                        }
                    });

                    icon_rl.addView(spinner);
                    spinner.setVisibility(Spinner.INVISIBLE);

                    final RelativeLayout icon_rl_rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams icon_rl_rl_lp = new RelativeLayout.LayoutParams((int) convertDpToPixel(57, context), (int) convertDpToPixel(72, context));
                    icon_rl_rl_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    icon_rl_rl.setLayoutParams(icon_rl_rl_lp);
                    icon_rl_rl.setId(R.id.settings_icon_id);
                    icon_rl_rl.setBackgroundColor(context.getResources().getColor(R.color.holoBlueMidnight));
                    icon_rl_rl.addView(icon_rl);


                    device_settings.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                icon_rl.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));

                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                icon_rl.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                spinner.performClick();
                            }
                            return true;
                        }
                    });

                    final TextView newName = new TextView(context);
                    TableRow.LayoutParams nnlp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .24f);
                    nnlp.setMargins(margin,margin,0,margin);
                    newName.setLayoutParams(nnlp);
                    nnlp.width = (0);
                    newName.setLines(2);
                    newName.setGravity(Gravity.CENTER);
                    newName.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                    newName.setText(geofence_name);
                    newName.setTextColor(Color.WHITE);

                    final TextView newLoc = new TextView(context);
                    TableRow.LayoutParams nllp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .565f);
                    nllp.setMargins(0, margin, 0, margin);
                    newLoc.setLayoutParams(nllp);
                    newLoc.setText(geofence_location);
                    nllp.width = (0);
                    newLoc.setLines(2);
                    newLoc.setGravity(Gravity.CENTER);
                    newLoc.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                    newLoc.setTextColor(Color.WHITE);

                    final TextView newRad = new TextView(context);
                    TableRow.LayoutParams nclp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .195f);
                    nclp.width=(0);
                    newRad.setGravity(Gravity.CENTER);
                    nclp.setMargins(0, margin, 0, margin);
                    newRad.setText(Html.fromHtml(geofence_area + " mi<sup>2</sup>"));
                    newRad.setLines(2);
                    newRad.setLayoutParams(nclp);
                    newRad.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                    newRad.setTextColor(Color.WHITE);

                    newLoc.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                newLoc.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                                newRad.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                                newName.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                                icon_rl.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                newLoc.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                newRad.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                newName.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                icon_rl.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                if (clickFunction.equalsIgnoreCase("None")) Toast.makeText(context, "Eye Of Horus", Toast.LENGTH_SHORT).show();
                                if (clickFunction.equalsIgnoreCase("MainMap")) {
                                    Intent intent=new Intent(context, MainMap.class);
                                    intent.putExtra("populate_single_geo", geofence_name);
                                    context.startActivity(intent);
                                }
                            }
                            return true;
                        }
                    });

                    newRad.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                newLoc.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                                newRad.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                                newName.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                                icon_rl.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                newLoc.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                newRad.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                newName.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                icon_rl.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                            }
                            return true;
                        }
                    });

                    newName.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                newLoc.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                                newRad.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                                newName.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                                icon_rl.setBackgroundColor(lighter(ContextCompat.getColor(context, R.color.recentUpdateGreen),(float)0.14));
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                newLoc.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                newRad.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                newName.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                                icon_rl.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                            }
                            return true;
                        }
                    });

                    TableRow newGeofenceRow = new TableRow(context);
                    LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    newGeofenceRow.addView(newName,nnlp);
                    newGeofenceRow.addView(newLoc, nllp);
                    newGeofenceRow.addView(newRad, nclp);
                    newGeofenceRow.setLayoutParams(lllp);
                    newGeofenceRow.setBackgroundColor(context.getResources().getColor(R.color.holoBlueMidnight));


                    final LinearLayout header_ll = new LinearLayout(context);
                    RelativeLayout.LayoutParams header_ll_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(72, context));
                    header_ll_lp.addRule(RelativeLayout.LEFT_OF, R.id.settings_icon_id);
                    header_ll.setLayoutParams(header_ll_lp);
                    header_ll.setBackgroundColor(ContextCompat.getColor(context, R.color.recentUpdateGreen));
                    header_ll.addView(newGeofenceRow);
                    header_ll.setClickable(true);

                    ViewGroup parentView = header_ll;
                    parentView.bringToFront();

                    RelativeLayout row_rl = new RelativeLayout(context);
                    RelativeLayout.LayoutParams row_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(72, context));
                    row_rl.setLayoutParams(row_lp);
                    row_rl.addView(icon_rl_rl);
                    row_rl.addView(header_ll);

                    layout.addView(row_rl);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Geofence Parse Error", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

}
