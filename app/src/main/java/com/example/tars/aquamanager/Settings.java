package com.example.tars.aquamanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Settings extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final SharedPreferences aqua_shared_prefs = getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);

        Button btn_cancel = (Button) findViewById(R.id.settings_cancel);
        Button btn_apply = (Button) findViewById(R.id.settings_apply);

        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Normal");
        spinnerArray.add("Satellite");
        spinnerArray.add("Hybrid");
        spinnerArray.add("Terrain");

        LinearLayout svll = (LinearLayout) findViewById(R.id.svrl);

        float d = getBaseContext().getResources().getDisplayMetrics().density;
        int den = (int) d;

        TextView settingsTitle = new TextView(this);
        settingsTitle.setText("Device Settings");
        settingsTitle.setPadding(8*den, 8*den, 8*den, 8*den);
        settingsTitle.setTextSize(27);
        settingsTitle.setTextColor(Color.rgb(168,212,255));
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        settingsTitle.setLayoutParams(params1);

        RelativeLayout rl1 = new RelativeLayout(this);
        rl1.addView(settingsTitle);
        RelativeLayout.LayoutParams rl1lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl1.setLayoutParams(rl1lp);

        ImageView divider = new ImageView(this);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 3*den);
        params2.setMargins(20*den, 3*den, 20*den, 3*den);
        params2.addRule(RelativeLayout.BELOW, settingsTitle.getId());
        divider.setLayoutParams(params2);
        //divider.setId(101);
        divider.setBackgroundColor(Color.rgb(0,188,255));

        TextView timezoneTitle = new TextView(this);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params3.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params3.addRule(RelativeLayout.CENTER_VERTICAL);
        timezoneTitle.setText("Timezone");
        timezoneTitle.setTextColor(Color.rgb(240,240,240));
        timezoneTitle.setPadding(8*den, 8*den, 8*den, 8*den);
        //timezoneTitle.setId(102);
        timezoneTitle.setTextSize(20);
        timezoneTitle.setLayoutParams(params3);

        TextView timezoneInfo1 = new TextView(this);
        timezoneInfo1.setPadding(8*den, 8*den, 8*den, 2*den);
        RelativeLayout.LayoutParams tzlp2 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tzlp2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        tzlp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        timezoneInfo1.setText(mTimeZone.getID());
        //timezoneInfo1.setId(103);
        timezoneInfo1.setTextColor(Color.rgb(220,220,220));
        timezoneInfo1.setTextSize(17);
        timezoneInfo1.setLayoutParams(tzlp2);

        TextView timezoneInfo2 = new TextView(this);
        RelativeLayout.LayoutParams tzlp3 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tzlp3.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        tzlp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //tzlp3.addRule(RelativeLayout.BELOW, timezoneInfo1.getId());
        timezoneInfo2.setText("GMT + 0" + TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS) + ":00");
        timezoneInfo2.setPadding(12*den, 2*den, 8*den, 2*den);
        timezoneInfo2.setLayoutParams(tzlp3);

        //timezoneInfo2.setId(104);
        timezoneInfo2.setTextColor(Color.rgb(220,220,220));
        timezoneInfo2.setTextSize(11);

        RelativeLayout tzll_info = new RelativeLayout(this);
        RelativeLayout.LayoutParams tzllinfolp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        tzllinfolp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tzll_info.setLayoutParams(tzllinfolp);
        tzll_info.addView(timezoneInfo1);
        tzll_info.addView(timezoneInfo2);

        RelativeLayout tzrl = new RelativeLayout(this);
        RelativeLayout.LayoutParams tzrllp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 70*den);
        tzrllp.addRule(RelativeLayout.CENTER_VERTICAL);
        tzrllp.addRule(RelativeLayout.BELOW, divider.getId());
        tzrl.setLayoutParams(tzrllp);
        tzrl.addView(timezoneTitle);
        tzrl.addView(tzll_info);

        TextView mapTypeTitle = new TextView(this);
        mapTypeTitle.setText("MainMap Display");
        mapTypeTitle.setPadding(8*den, 12*den, 8*den, 8*den);
        //mapTypeTitle.setId(113);
        mapTypeTitle.setTextColor(Color.rgb(240,240,240));
        mapTypeTitle.setTextSize(20);
        RelativeLayout.LayoutParams params14 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //params14.addRule(RelativeLayout.BELOW, checkboxHideSimilarMarkers.getId());
        params14.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mapTypeTitle.setLayoutParams(params14);

        final Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        RelativeLayout.LayoutParams params15 = new RelativeLayout.LayoutParams(200*den, 45*den);
        //params15.addRule(RelativeLayout.BELOW, checkboxHideSimilarMarkers.getId());
        params15.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params15.setMargins(0, 10*den, 0, 0);
        spinner.setPadding(8*den, 8*den, 8*den, 8*den);
        //spinner.setId(114);
        spinner.setLayoutParams(params15);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(aqua_shared_prefs.getInt("!set_mapDisplayType", 0));

        RelativeLayout mdrl = new RelativeLayout(this);
        RelativeLayout.LayoutParams mdrllp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 80*den);
        mdrllp.addRule(RelativeLayout.CENTER_VERTICAL);
        mdrl.setLayoutParams(mdrllp);
        mdrl.addView(mapTypeTitle);
        mdrl.addView(spinner);

        final EditText numMarkers = new EditText(this);
        RelativeLayout.LayoutParams paramsNumMarkers = new RelativeLayout.LayoutParams(80 * den, LayoutParams.WRAP_CONTENT);
        paramsNumMarkers.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        numMarkers.setLayoutParams(paramsNumMarkers);
        //numMarkers.setGravity(Gravity.CENTER);
        numMarkers.setText(aqua_shared_prefs.getString("!set_numberOfMarkersToShow", "?"));
        numMarkers.setPadding(8*den, 15*den, 8*den, 8*den);
        //numMarkers.setId(130);
        numMarkers.setFilters(new InputFilter[]{ new InputFilterMinMax("0", "1000")});
        numMarkers.setInputType(InputType.TYPE_CLASS_NUMBER);

        TextView tv_numMarkers = new TextView(this);
        tv_numMarkers.setText("No. Markers Per Device");
        tv_numMarkers.setPadding(8*den, 8*den, 8*den, 8*den);
        //tv_numMarkers.setId(131);
        tv_numMarkers.setTextSize(20);
        tv_numMarkers.setTextColor(Color.rgb(240,240,240));
        RelativeLayout.LayoutParams paramsNumMarkTitle = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //paramsNumMarkTitle.addRule(RelativeLayout.BELOW, divider2.getId());
        paramsNumMarkTitle.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tv_numMarkers.setLayoutParams(paramsNumMarkTitle);

        RelativeLayout nmrl = new RelativeLayout(this);
        RelativeLayout.LayoutParams nmrllp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 80*den);
        nmrllp.addRule(RelativeLayout.CENTER_VERTICAL);
        nmrl.setLayoutParams(nmrllp);
        nmrl.addView(tv_numMarkers);
        nmrl.addView(numMarkers);

        TextView showGeos_tv = new TextView(this);
        showGeos_tv.setText("Show Geofences On MainMap");
        showGeos_tv.setPadding(8*den, 8*den, 8*den, 8*den);
        //tv_numMarkers.setId(131);
        showGeos_tv.setTextSize(20);
        showGeos_tv.setTextColor(Color.rgb(240,240,240));
        RelativeLayout.LayoutParams showGeos_tv_lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //paramsNumMarkTitle.addRule(RelativeLayout.BELOW, divider2.getId());
        showGeos_tv_lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        showGeos_tv.setLayoutParams(paramsNumMarkTitle);

        final CheckBox checkboxHideSimilarMarkers = new CheckBox(this);
        //checkboxHideSimilarMarkers.setText("Hide Similar Markers");
        //checkboxHideSimilarMarkers.setPaddingRelative(0, 20*den, 0, 0);

        //checkboxHideSimilarMarkers.setId(120);
        //checkboxHideSimilarMarkers.setTextSize(20);
        RelativeLayout.LayoutParams params28 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //params28.addRule(RelativeLayout.BELOW, numMarkers.getId());
        params28.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //params28.setMargins(0, 0*den, 0, 0);
        checkboxHideSimilarMarkers.setLayoutParams(params28);
        checkboxHideSimilarMarkers.setChecked(aqua_shared_prefs.getBoolean("!set_showGeos", false));

        if((aqua_shared_prefs.getBoolean("!set_showGeos", false))) Log.d("bool", "yes");
        else Log.d("bool", "no");


        RelativeLayout sgrl = new RelativeLayout(this);
        RelativeLayout.LayoutParams sgrllp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 80*den);
        sgrllp.addRule(RelativeLayout.CENTER_VERTICAL);
        sgrl.setLayoutParams(nmrllp);
        sgrl.addView(checkboxHideSimilarMarkers);
        sgrl.addView(showGeos_tv);

        svll.addView(rl1);
        svll.addView(divider);
        svll.addView(tzrl);
        svll.addView(mdrl);
        svll.addView(nmrl);
        svll.addView(sgrl);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numMarkersVal = numMarkers.getText().toString();
                boolean showGeos = checkboxHideSimilarMarkers.isChecked();

                aqua_shared_prefs.edit().putInt("!set_mapDisplayType", spinner.getSelectedItemPosition()).apply();
                aqua_shared_prefs.edit().putString("!set_numberOfMarkersToShow", numMarkersVal).apply();
                aqua_shared_prefs.edit().putBoolean("!set_showGeos", showGeos).apply();

                finish();
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Settings Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.tars.aquamanager/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Settings Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.tars.aquamanager/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

}
