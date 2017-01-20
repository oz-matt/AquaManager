package com.example.tars.aquamanager;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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

public class GetLocationOnly extends AsyncTask<String, JSONObject, String> {

    private Activity mContext;
    private ProgressDialog mDialog;

    static HttpURLConnection urlConnection;

    private String name = "";
    private String latstr = "";
    private String lonstr = "";
    private String geotype = "";
    private String geodata = "";
    private String area = "";

    public GetLocationOnly(Activity context) {
        mContext = context;
        mDialog = new ProgressDialog(context);
        this.mDialog.setCancelable(false);
    }

    protected void onPreExecute() {
        this.mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.mDialog.show();
        this.mDialog.setContentView(R.layout.progressdialog);
        this.mDialog.getWindow().setGravity(Gravity.CENTER);
    }

    protected String doInBackground(final String... input) {
        try {

            // Input String in the form of input[0] = lat, input[1] = lon, input[2] = <name of geofence>, input[3] = type of geofence, input[4] = data of geofence separated by commas, input[5] is the area in sq miles

            Double lat = Double.parseDouble(input[0]);
            Double lon = Double.parseDouble(input[1]);

            latstr = input[0];
            lonstr = input[1];
            name = input[2];
            geotype = input[3];
            geodata = input[4];
            area = input[5];

            //If geotype = circle, geodata just = the radius
            //If geotype = polygon, geodata = a List<LatLng> of 8 points.

            JSONObject ret = getLocation(lat, lon);
            Log.d("loc123", ret.toString());
            JSONObject location;
            String location_string = "";

            if (ret.getJSONArray("results").length() > 0) {
                location = ret.getJSONArray("results").getJSONObject(2);
                location_string = location.getString("formatted_address");
                Log.d("test", "formatted address:" + location_string);

                return location_string;
            } else {
                JSONObject ret_nat = getNaturalLocation(lat, lon);
                Log.d("ret_nat", ret.toString());
                if (ret.getJSONArray("results").length() > 0) {

                }

                return "error";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    protected void onPostExecute(final String result) {

        mDialog.dismiss();

        JSONObject geo_settings = new JSONObject();

        SharedPreferences aqua_shared_prefs = mContext.getSharedPreferences("aqua_shared_prefs", mContext.MODE_PRIVATE);

        try {

            if (result.equalsIgnoreCase("error")) {
                Toast.makeText(mContext, "Error Getting Location", Toast.LENGTH_SHORT).show();
                geo_settings.put("location", "Uncharted");
            } else {
                geo_settings.put("location", result);
            }

            geo_settings.put("area", area);
            geo_settings.put("geotype", geotype);
            geo_settings.put("geodata", geodata);
            geo_settings.put("lat", latstr);
            geo_settings.put("lon", lonstr);

            aqua_shared_prefs.edit().putString("!geo_" + name + "_settings", geo_settings.toString()).apply();

        } catch (Exception e) {
            Toast.makeText(mContext, "Geofence Parse Error", Toast.LENGTH_SHORT).show();
        }

        Log.d("omg", result);

        mContext.finish();

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

    private static JSONObject getNaturalLocation (double lat, double lon) {

        StringBuilder result = new StringBuilder();

        try {
            //URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&result_type=natural_feature&key=AIzaSyB8o5GaUMIgkV4mORr-UdXo7_bVavdm1nE");
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&result_type=natural_feature&key=AIzaSyAeHtCDX8llqpxW-xOHZ-nyBPHvKGDeOIw");
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
