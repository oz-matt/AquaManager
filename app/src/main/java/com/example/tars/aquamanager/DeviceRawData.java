package com.example.tars.aquamanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class DeviceRawData extends Activity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_raw_data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAqsensTable();
    }

    private void refreshAqsensTable () {
        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        final TableLayout raw_rl = (TableLayout) findViewById(R.id.drd_tl);

        raw_rl.removeAllViews();

        context = this;

        final String name = getIntent().getStringExtra("name");

        String rawaqsensstr = aqua_shared_prefs.getString("!dev_" + name + "_aqsens", "Not Found");

        try {
            JSONArray aqsens_json_array = new JSONArray(rawaqsensstr);

            for (int i = 0; i < aqsens_json_array.length(); i++) {
                JSONObject single_aqsen = aqsens_json_array.getJSONObject(i);
                JSONObject gpsminimum = single_aqsen.getJSONObject("gpsminimum");

                String time_raw = gpsminimum.getString("time").split("\\.")[0] + "Z";

                SimpleDateFormat dfp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                dfp.setTimeZone(TimeZone.getTimeZone("GMT"));

                Date dateprev = dfp.parse(time_raw);

                RelativeLayout row_rl = new RelativeLayout(context);
                RelativeLayout.LayoutParams row_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) AquaUtil.convertDpToPixel(50, context));
                row_rl.setLayoutParams(row_lp);

                row_rl.setBackgroundColor(getResources().getColor(R.color.holoBlueDark));
                row_rl.setPadding(2,2,2,2);

                final ImageButton paper_img = new ImageButton(context);
                paper_img.setImageResource(R.drawable.paper_wht);
                paper_img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                paper_img.setBackgroundColor(getResources().getColor(R.color.holoBlue));
                paper_img.setPadding(0,0,1,0);
                RelativeLayout.LayoutParams nilp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                nilp.width = (int) AquaUtil.convertDpToPixel(30, context);
                nilp.height = (int) AquaUtil.convertDpToPixel(30, context);
                nilp.addRule(RelativeLayout.CENTER_IN_PARENT);
                paper_img.setLayoutParams(nilp);

                int margin = (int) context.getResources().getDisplayMetrics().density;

                final RelativeLayout icon_rl = new RelativeLayout(context);
                TableRow.LayoutParams icon_rl_lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .3f);
                icon_rl.setLayoutParams(icon_rl_lp);
                icon_rl_lp.setMargins(0,margin,margin,margin);
                icon_rl.setBackgroundColor(getResources().getColor(R.color.holoBlue));
                icon_rl.addView(paper_img);

                final TextView elvtv = new TextView(getBaseContext());
                String set = "(" + (i+1) + ")  " + dateprev.toString();
                elvtv.setText(set);
                elvtv.setTextColor(getResources().getColor(R.color.white));
                elvtv.setTextSize(AquaUtil.convertDpToPixel(10, context));
                TableRow.LayoutParams tvrl = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, .7f);
                elvtv.setLayoutParams(tvrl);
                elvtv.setGravity(Gravity.CENTER_VERTICAL);
                elvtv.setPadding(10,0,0,0);

                final TableRow newModuleRow = new TableRow(context);
                TableLayout.LayoutParams lllp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, (int) AquaUtil.convertDpToPixel(100, context));
                lllp.setMargins(2,2,2,2);
                newModuleRow.setLayoutParams(lllp);
                newModuleRow.setBackgroundColor(getResources().getColor(R.color.holoBlue));

                View spacerColumn = new View(context);
                newModuleRow.addView(spacerColumn, new TableRow.LayoutParams(1, (int) AquaUtil.convertDpToPixel(50, context)));

                newModuleRow.addView(icon_rl);
                newModuleRow.addView(elvtv);

                newModuleRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String num_chunk = elvtv.getText().toString().substring(1,2);
                        int aqsen_ele = Integer.valueOf(num_chunk) - 1;
                        Intent intent = new Intent(context, ViewRawData.class);
                        intent.putExtra("aqsens_pref", "!dev_" + name + "_aqsens");
                        intent.putExtra("aqsens_ele", aqsen_ele);
                        context.startActivity(intent);
                    }
                });

                raw_rl.addView(newModuleRow);

                icon_rl.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        newModuleRow.setBackgroundColor(AquaUtil.lighter(getResources().getColor(R.color.holoBlue),(float)0.14));
                        icon_rl.setBackgroundColor(AquaUtil.lighter(getResources().getColor(R.color.holoBlue),(float)0.14));
                        paper_img.setBackgroundColor(AquaUtil.lighter(getResources().getColor(R.color.holoBlue),(float)0.14));
                        return false;
                    }
                });

                elvtv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        newModuleRow.setBackgroundColor(AquaUtil.lighter(getResources().getColor(R.color.holoBlue),(float)0.14));
                        icon_rl.setBackgroundColor(AquaUtil.lighter(getResources().getColor(R.color.holoBlue),(float)0.14));
                        paper_img.setBackgroundColor(AquaUtil.lighter(getResources().getColor(R.color.holoBlue),(float)0.14));
                        return false;
                    }
                });

                paper_img.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        newModuleRow.setBackgroundColor(AquaUtil.lighter(getResources().getColor(R.color.holoBlue),(float)0.14));
                        icon_rl.setBackgroundColor(AquaUtil.lighter(getResources().getColor(R.color.holoBlue),(float)0.14));
                        paper_img.setBackgroundColor(AquaUtil.lighter(getResources().getColor(R.color.holoBlue),(float)0.14));
                        return false;
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "AQSENS Parse Error", Toast.LENGTH_SHORT).show();
        }
    }

}
