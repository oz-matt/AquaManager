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

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by TARS on 6/7/2016.
 */
public class QServerConnect extends AsyncTask<JSONObject, JSONObject, String[]> {

    private Activity mContext;
    private ProgressDialog mDialog;

    static HttpURLConnection urlConnection;

    public interface AsyncResponse {
        void processFinish(String[] output);
    }

    public AsyncResponse delegate = null;

    public QServerConnect(Activity context, AsyncResponse delegate) {
        mContext = context;
        mDialog = new ProgressDialog(context);
        this.delegate = delegate;
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

            String pct_batt = "N/A";

            try {

                JSONObject dummy = new JSONObject(response);
                String aqsens_str = dummy.getString("aqsens");

                Log.d("Tag", aqsens_str);

                JSONArray aqsens_obj = new JSONArray(aqsens_str);

                JSONObject latest_element = aqsens_obj.getJSONObject(0);
                JSONObject latest_gps_minimum = latest_element.getJSONObject("gpsminimum");
                JSONObject sensors = latest_element.getJSONObject("sensors");

                pct_batt = sensors.getString("pct_battery");

                String latest_lon = latest_gps_minimum.getString("lon");
                String latest_lat = latest_gps_minimum.getString("lat");

                Double lon = Double.parseDouble(latest_lon);
                Double lat = Double.parseDouble(latest_lat);


                JSONObject ret = getLocation(lat, lon);
                JSONObject location;
                String location_string = "";

                location = ret.getJSONArray("results").getJSONObject(2);
                location_string = location.getString("formatted_address");
                Log.d("test", "formatted address:" + location_string);

                String[] retu = {response, location_string, pct_batt};

                return retu;

            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] retu = {response, "<no data>", pct_batt};
            return retu;

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] retu = {"failed", "", ""};

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