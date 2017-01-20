package com.example.tars.aquamanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;

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
import java.util.Map;

public class QServerGetAqsens extends AsyncTask<String, String, String> {
    private Activity mContext;
    private ProgressDialog mDialog;

    static HttpURLConnection urlConnection;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public QServerGetAqsens(Activity context, AsyncResponse delegate) {
        mContext = context;
        mDialog = new ProgressDialog(context);
        this.mDialog.setCancelable(false);
        this.delegate = delegate;
    }

    protected void onPreExecute() {
        this.mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.mDialog.show();
        this.mDialog.setContentView(R.layout.progressdialog);
        this.mDialog.getWindow().setGravity(Gravity.CENTER);
    }

    protected String doInBackground(String... str) {

        int ndevices = 0;

        try {

            final SharedPreferences aqua_shared_prefs = mContext.getSharedPreferences("aqua_shared_prefs", mContext.MODE_PRIVATE);

            final String iid = aqua_shared_prefs.getString("iid", "Not Found");

            Map<String, ?> allEntries = aqua_shared_prefs.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                if (entry.getKey().startsWith("!dev_") && entry.getKey().endsWith("_qdata")) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    String name = key.substring(5, key.length() - 6);

                    ndevices++;

                    JSONObject dummy_json = new JSONObject(value);
                    String aquakey = dummy_json.getString("aquakey");
                    String passcode = dummy_json.getString("pass");

                    JSONObject outgoing_json = new JSONObject();

                    try {
                        outgoing_json.put("reqtype", "getaqsen");
                        outgoing_json.put("aquakey", aquakey);
                        outgoing_json.put("pass", passcode);
                        outgoing_json.put("iid", iid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("outgoing json", outgoing_json.toString());

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
                    os.write(outgoing_json.toString().getBytes());
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

                    JSONObject dummy = new JSONObject(response);
                    String aqsens_str = dummy.getString("qdata");

                    aqua_shared_prefs.edit().putString("!dev_" + name + "_aqsens", aqsens_str).commit();

                    JSONArray aqsens_obj = new JSONArray(aqsens_str);

                    JSONObject latest_element = aqsens_obj.getJSONObject(0);
                    JSONObject latest_gps_minimum = latest_element.getJSONObject("gpsminimum");
                    JSONObject sensors = latest_element.getJSONObject("sensors");

                    String pct_batt = sensors.getString("pct_battery");

                    String latest_lon = latest_gps_minimum.getString("lon");
                    String latest_lat = latest_gps_minimum.getString("lat");

                    Double lon = Double.parseDouble(latest_lon);
                    Double lat = Double.parseDouble(latest_lat);

                    JSONObject ret = getLocation(lat, lon);
                    JSONObject location;
                    String location_string = "";

                    aqua_shared_prefs.edit().putString("!dev_" + name + "_pctbat", pct_batt).commit();

                    try {
                        location = ret.getJSONArray("results").getJSONObject(2);
                        location_string = location.getString("formatted_address");
                        Log.d("test", "formatted address:" + location_string);

                        aqua_shared_prefs.edit().putString("!dev_" + name + "_locstr", location_string).commit();

                    } catch (Exception e) {
                        e.printStackTrace();
                        aqua_shared_prefs.edit().putString("!dev_" + name + "_locstr", "N/A").commit();
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "cxn error";
        }

        if (ndevices > 0) return "success";
        else return "no devs";
    }

    @Override
    protected void onPostExecute(final String result) {

        mDialog.dismiss();

        delegate.processFinish(result);
    }

    private static JSONObject getLocation (double lat, double lon) {

        StringBuilder result = new StringBuilder();

        try {
            //URL url = new URL("http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lon+"&sensor=true");
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" +lat+ "," + lon + "&key=AIzaSyAeHtCDX8llqpxW-xOHZ-nyBPHvKGDeOIw");
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
