package com.example.tars.aquamanager;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by TARS on 6/7/2016.
 */
public class QServerConnect extends AsyncTask<JSONObject, JSONObject, String[]> {

    private Activity mContext;
    private ProgressDialog mDialog;

    static HttpURLConnection urlConnection;

    private boolean needLocation = false;

    public interface AsyncResponse {
        void processFinish(String[] output);
    }

    public AsyncResponse delegate = null;

    public QServerConnect(Activity context, boolean needloc, AsyncResponse delegate) {
        mContext = context;
        mDialog = new ProgressDialog(context);
        this.delegate = delegate;
        needLocation = needloc;
    }

    protected void onPreExecute() {
        this.mDialog.show();
    }

    protected String[] doInBackground(final JSONObject... outgoing_json) {
        try {

            JSONObject msg = outgoing_json[0];

            URL url = new URL("http://198.61.169.55:8081");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            conn.connect();

            OutputStream os = new BufferedOutputStream(conn.getOutputStream());
            os.write(msg.toString().getBytes());
            os.flush();

            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(is));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();

            is.close();
            os.close();

            String pct_batt = "<no data>";

            if(needLocation) {

                try {

                    JSONObject dummy = new JSONObject(response);
                    String aqsens_str = dummy.getString("aqsens");

                    Log.d("Tag", aqsens_str);

                    JSONArray aqsens_obj = new JSONArray(aqsens_str);

                    JSONObject latest_element = aqsens_obj.getJSONObject(0);

                    JSONObject latest_gps_minimum = latest_element.getJSONObject("gpsminimum");
                    JSONObject sensors = latest_element.getJSONObject("sensors");

                    String temp = "<no data>";
                    String humidity = "<no data>";

                    String height = "<no data>";
                    String speed = "<no data>";
                    String direction = "<no data>";
                    String nums = "<no data>";
                    String latest_time = "<no data>";
                    String second_latest_time = "<no data>";

                    pct_batt = sensors.getString("pct_battery");
                    temp = sensors.getString("temperature");
                    humidity = sensors.getString("humidity");

                    height = latest_gps_minimum.getString("height");
                    speed = latest_gps_minimum.getString("gspeed");
                    direction = latest_gps_minimum.getString("direction");
                    nums = latest_gps_minimum.getString("numsat");
                    latest_time = latest_gps_minimum.getString("time").split("\\.")[0] + "Z";

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    df.setTimeZone(TimeZone.getTimeZone("GMT"));

                    Date date = df.parse(latest_time);

                    String latest_time_formatted = date.toString();

                    String latest_lon = latest_gps_minimum.getString("lon");
                    String latest_lat = latest_gps_minimum.getString("lat");

                    Double lon = Double.parseDouble(latest_lon);
                    Double lat = Double.parseDouble(latest_lat);

                    JSONObject ret = getLocation(lat, lon);
                    JSONObject location, location_long;
                    String location_string = "", location_long_string = "", prev_location_long_string = "<no data>";

                    location = ret.getJSONArray("results").getJSONObject(2);
                    location_string = location.getString("formatted_address");

                    location_long = ret.getJSONArray("results").getJSONObject(0);
                    location_long_string = location_long.getString("formatted_address");

                    String prev_time_formatted = "<no data>";

                    if (aqsens_obj.length() > 1) {
                        JSONObject second_latest_element = aqsens_obj.getJSONObject(1);
                        JSONObject second_latest_gps_minimum = second_latest_element.getJSONObject("gpsminimum");
                        String second_latest_lon = second_latest_gps_minimum.getString("lon");
                        String second_latest_lat = second_latest_gps_minimum.getString("lat");

                        Double prev_lon = Double.parseDouble(second_latest_lon);
                        Double prev_lat = Double.parseDouble(second_latest_lat);

                        JSONObject retprev = getLocation(prev_lat, prev_lon);
                        JSONObject prev_location_long = retprev.getJSONArray("results").getJSONObject(0);
                        prev_location_long_string = prev_location_long.getString("formatted_address");

                        second_latest_time = second_latest_gps_minimum.getString("time").split("\\.")[0] + "Z";

                        SimpleDateFormat dfp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        dfp.setTimeZone(TimeZone.getTimeZone("GMT"));

                        Date dateprev = dfp.parse(second_latest_time);

                        prev_time_formatted = dateprev.toString();

                    }

                    String full_settings_str = location_long_string + "!" + temp + "!" + humidity + "!" + height + "!" + speed + "!" + direction + "!" + nums + "!" + latest_time_formatted + "!" + prev_time_formatted;

                    Log.d("test333", "formatted address:" + location_string);
                    Log.d("test334", "formatted address:" + location_long_string);

                    String[] retu = {response, location_string, full_settings_str, prev_location_long_string, pct_batt};

                    return retu;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            String[] retu = {response, "<no data>", "<no data>", pct_batt};
            return retu;

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] retu = {"failed", "", "", ""};

        return retu;
    }

    @Override
    protected void onPostExecute(final String[] result) {

        mDialog.dismiss();

        delegate.processFinish(result);

    }

    private static JSONObject getLocation (double lat, double lon) {

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL("http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lon+"&sensor=true");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            urlConnection.disconnect();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(result.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;

        } catch( Exception e) {
            e.printStackTrace();

            urlConnection.disconnect();
        }

        return null;
    }
}