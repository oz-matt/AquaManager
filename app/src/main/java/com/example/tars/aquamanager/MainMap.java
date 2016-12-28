package com.example.tars.aquamanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<String> devs_to_populate = new ArrayList<>();
    ArrayList<String> geos_to_populate = new ArrayList<>();

    private static Resources static_resources;
    private static String pkg_name;

    List<Marker> markers = new ArrayList<Marker>();
    Context mContext;
    AlertDialog dialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (!devs_to_populate.isEmpty()) devs_to_populate.clear();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        static_resources = getResources();
        pkg_name = getPackageName();

        mContext = this;

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        final SharedPreferences aqua_shared_prefs = this.getSharedPreferences("aqua_shared_prefs", this.MODE_PRIVATE);

        int terrain_type = aqua_shared_prefs.getInt("!set_mapDisplayType", 0);

        if (terrain_type == 0) mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        else if (terrain_type == 1) mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else if (terrain_type == 2) mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);

        ImageButton zoom_in = (ImageButton) findViewById(R.id.zoomin);
        ImageButton zoom_out = (ImageButton) findViewById(R.id.zoomout);
        ImageButton add_dev_btn = (ImageButton) findViewById(R.id.add_device_ibtn);

        final RelativeLayout zoom_in_rl = (RelativeLayout) findViewById(R.id.zoomin_rl);
        final RelativeLayout zoom_out_rl = (RelativeLayout) findViewById(R.id.zoomout_rl);
        final RelativeLayout adddev_rl = (RelativeLayout) findViewById(R.id.add_dev_rl);

        if (getIntent().hasExtra("populate_extra")) {
            String populate_extra = getIntent().getStringExtra("populate_extra");
            if (populate_extra.equalsIgnoreCase("all")) {
                boolean atleastone = false;
                if (addAllDevicesWithDisplayFlagToDevs_To_PopulateList() > 0) {
                    populateMarkersForDevicesInDevs_To_PopulateList();
                    atleastone = true;
                }
                if (settingsAllowGeofencesToBeShownOnWorldMap()) {
                    if (addAllGeofencesToGeos_To_PopulateList() > 0) {
                        populateMarkersForGeofencesInGeos_To_PopulateList();
                        atleastone = true;
                    }
                }
                if (atleastone) ZoomToFitMarkers(Float.parseFloat("0"));
            } else {
                String name = populate_extra.substring(5);
                addSingleDeviceToDevs_To_PopulateList(name);
                populateMarkersForDevicesInDevs_To_PopulateList();
                ZoomToFitMarkers(Float.parseFloat("0"));
            }
        } else if (getIntent().hasExtra("populate_single_geo")) {
            String geo = getIntent().getStringExtra("populate_single_geo");
            try {

                String this_geos_settings = aqua_shared_prefs.getString("!geo_" + geo + "_settings", "NotFound");
                JSONObject settings_obj = new JSONObject(this_geos_settings);
                Float rad = Float.parseFloat(settings_obj.getString("radius"));

                addSingleGeofenceToGeos_To_PopulateList(geo);
                populateMarkersForGeofencesInGeos_To_PopulateList();
                ZoomToFitMarkers(rad);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Geo Parse Failure", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No devices to populate", Toast.LENGTH_SHORT).show();
        }

        zoom_in.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zoom_in_rl.setBackgroundResource(R.color.colorAqua);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    zoom_in_rl.setBackgroundResource(R.color.whiteTransparent);
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
                return true;
            }
        });

        zoom_out.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    zoom_out_rl.setBackgroundResource(R.color.colorAqua);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    zoom_out_rl.setBackgroundResource(R.color.whiteTransparent);
                    mMap.animateCamera(CameraUpdateFactory.zoomOut());
                }
                return true;
            }
        });

        add_dev_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    adddev_rl.setBackgroundResource(R.color.colorAqua);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    adddev_rl.setBackgroundResource(R.color.whiteTransparent);

                    final SharedPreferences aqua_shared_prefs = mContext.getSharedPreferences("aqua_shared_prefs", mContext.MODE_PRIVATE);
                    java.util.Map<String, ?> keys = aqua_shared_prefs.getAll();

                    final List<String> temp_array_of_devs = new ArrayList<>();
                    List<Boolean> temp_array_of_dev_booleans = new ArrayList<>();

                    for (java.util.Map.Entry<String, ?> entry : keys.entrySet()) {
                        String key = entry.getKey();
                        if (key.endsWith("qdata")) {
                            String name = key.substring(5, key.length() - 6);
                            temp_array_of_devs.add(name);

                            boolean is_in_devs_to_populate = false;

                            for (String temp : devs_to_populate) {
                                if (temp.equalsIgnoreCase(name)) {
                                    is_in_devs_to_populate = true;
                                }
                            }
                            temp_array_of_dev_booleans.add(is_in_devs_to_populate);
                        }
                    }

                    CharSequence[] items = temp_array_of_devs.toArray(new CharSequence[0]);
                    boolean[] isChecked = new boolean[temp_array_of_dev_booleans.size()];
                    for (int i = 0; i < temp_array_of_dev_booleans.size(); i++) {
                        isChecked[i] = temp_array_of_dev_booleans.get(i);
                    }
                    final ArrayList selectedItems = new ArrayList();

                    if (temp_array_of_devs.isEmpty()) {
                        AlertDialog dialog = new AlertDialog.Builder(mContext)
                                .setTitle("No Devices To Add")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create();
                        dialog.show();
                    } else {

                        dialog = new AlertDialog.Builder(mContext)
                                .setTitle("Select Devices To Display")
                                .setMultiChoiceItems(items, isChecked, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                        if (isChecked) {
                                            // If the user checked the item, add it to the selected items

                                            selectedItems.add(indexSelected);
                                            Log.d("DeG", selectedItems.toString());
                                        } else if (selectedItems.contains(indexSelected)) {
                                            // Else, if the item is already in the array, remove it
                                            selectedItems.remove(Integer.valueOf(indexSelected));
                                        }
                                    }
                                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {

                                        //  Your code when user clicked on OK
                                        //  You can write the code  to save the selected item here
                                        //Log.d("DeK", selectedItems.toString());
                                        getCheckedNamesAndRepopulateDevs_To_Populate(temp_array_of_devs);

                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //  Your code when user clicked on Cancel
                                    }
                                }).create();
                        dialog.show();

                    }

                }
                return true;
            }
        });

    }

    private void populateMarkersForDevicesInDevs_To_PopulateList() {
        final SharedPreferences aqua_shared_prefs = this.getSharedPreferences("aqua_shared_prefs", this.MODE_PRIVATE);
        for (String name : devs_to_populate) {
            try {
                String this_devices_settings = aqua_shared_prefs.getString("!dev_" + name + "_settings", "NotFound");
                JSONObject settings_obj = new JSONObject(this_devices_settings);
                String this_devices_marker_color = settings_obj.getString("MarkerColor");
                Integer num_markers_to_display = Integer.valueOf(settings_obj.getString("MaxMarkersToDisplay"));

                String this_aqsens = aqua_shared_prefs.getString("!dev_" + name + "_aqsens", "Not Found");
                JSONArray aqsens_json_array = new JSONArray(this_aqsens);

                for (int i = 0; i < aqsens_json_array.length(); i++) {
                    JSONObject this_aqsens_element = aqsens_json_array.getJSONObject(i);
                    JSONObject this_elements_gpsminimum = this_aqsens_element.getJSONObject("gpsminimum");
                    Double lat = Double.parseDouble(this_elements_gpsminimum.getString("lat"));
                    Double lon = Double.parseDouble(this_elements_gpsminimum.getString("lon"));

                    String date = this_aqsens_element.getString("datetime");

                    LatLng markerSpot = new LatLng(lat, lon);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerSpot).title(date).snippet("Going x mph").icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(this_devices_marker_color))));
                    float step = (0.7f) / (aqsens_json_array.length());
                    marker.setAlpha(1 - (step * i));
                    markers.add(marker);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "JSON Aqsens Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void populateMarkersForGeofencesInGeos_To_PopulateList() {
        final SharedPreferences aqua_shared_prefs = this.getSharedPreferences("aqua_shared_prefs", this.MODE_PRIVATE);
        for (String name : geos_to_populate) {
            try {
                Log.d("gdep", name);
                String this_geos_settings = aqua_shared_prefs.getString("!geo_" + name + "_settings", "NotFound");
                JSONObject settings_obj = new JSONObject(this_geos_settings);

                String geotype_str = settings_obj.getString("geotype");

                if (geotype_str.equalsIgnoreCase("circle")) {
                    Double lat = Double.parseDouble(settings_obj.getString("lat"));
                    Double lon = Double.parseDouble(settings_obj.getString("lon"));

                    Double rad = Double.parseDouble(settings_obj.getString("geodata"));

                    LatLng geocenter = new LatLng(lat, lon);

                    CircleOptions circleOptions = new CircleOptions()
                            .center(geocenter)
                            .radius(rad * 1609.34)
                            .fillColor(0x7082BEF4)
                            .strokeColor(Color.TRANSPARENT)
                            .strokeWidth(2);

                    mMap.addCircle(circleOptions);

                    LatLng markerSpot = new LatLng(lat, lon);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerSpot).title(name).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("flagc", (int) convertDpToPixel(70, this), (int) convertDpToPixel(49, this)))));

                    markers.add(marker);
                } else if (geotype_str.equalsIgnoreCase("polygon")) {

                    Polygon polygon;
                    List<LatLng> box_latlngs = new ArrayList<>();

                    JSONArray latlng_array = new JSONArray(settings_obj.getString("geodata"));

                    for (int i = 0; i<latlng_array.length(); i++) {
                        JSONObject latlng_obj = latlng_array.getJSONObject(i);
                        String lat = latlng_obj.getString("lat");
                        String lon = latlng_obj.getString("lon");
                        LatLng ll = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                        box_latlngs.add(i, ll);
                    }

                    PolygonOptions rectOptions = new PolygonOptions().addAll(box_latlngs);

                    polygon = mMap.addPolygon(rectOptions);
                    polygon.setStrokeColor(R.color.holoBlue);
                    polygon.setFillColor(R.color.holoBlueLitTransparent);
                } else {
                    Toast.makeText(this, "Geofence Corrupted", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Geo JSON Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(static_resources, static_resources.getIdentifier(iconName, "drawable", pkg_name));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private int addAllDevicesWithDisplayFlagToDevs_To_PopulateList() {

        int num_devices = 0;

        final SharedPreferences aqua_shared_prefs = this.getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);
        java.util.Map<String, ?> keys = aqua_shared_prefs.getAll();
        for (java.util.Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("cdc", "key:" + entry.getKey() + ", val:" + entry.getValue().toString());
            if (entry.getKey().endsWith("qdata")) {
                num_devices++;
                String name = entry.getKey().substring(5, entry.getKey().length() - 6);
                String settings_for_this_device = aqua_shared_prefs.getString("!dev_" + name + "_settings", "Not Found");
                try {
                    Log.d("cdx", "!dev_" + name + "_settings");
                    JSONObject settings_obj = new JSONObject(settings_for_this_device);
                    String should_we_display_this_dev = settings_obj.getString("DisplayFlag");
                    if (should_we_display_this_dev.equalsIgnoreCase("true")) {
                        devs_to_populate.add(name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "JSON Settings Error 2", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return num_devices;
    }

    private int addAllGeofencesToGeos_To_PopulateList() {
        int num_Geos = 0;

        final SharedPreferences aqua_shared_prefs = this.getSharedPreferences("aqua_shared_prefs", MODE_PRIVATE);
        java.util.Map<String, ?> keys = aqua_shared_prefs.getAll();
        for (java.util.Map.Entry<String, ?> entry : keys.entrySet()) {

            if (entry.getKey().startsWith("!geo_")) {
                num_Geos++;
                String name = entry.getKey().substring(5, entry.getKey().length() - 9);
                geos_to_populate.add(name);
            }
        }

        return num_Geos;
    }

    private void ZoomToFitMarkers(final Float rad) {
        final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressLint("NewApi")
                @Override
                public void onGlobalLayout() {
                    LatLngBounds.Builder bld = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        bld.include(marker.getPosition());
                    }
                    LatLngBounds bounds = bld.build();

                    int padding = 200; // offset from edges of the map in pixels

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.moveCamera(cu);

                    if (markers.size() == 1) mMap.moveCamera(CameraUpdateFactory.zoomTo(13.5f - (rad * 0.4f)));
                }
            });
        }
    }

    private void addSingleDeviceToDevs_To_PopulateList(String name) {
        devs_to_populate.add(name);
    }

    private void addSingleGeofenceToGeos_To_PopulateList(String name) {
        geos_to_populate.add(name);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    private void getCheckedNamesAndRepopulateDevs_To_Populate(List<String> devlist) {
        SparseBooleanArray sba = dialog.getListView().getCheckedItemPositions();
        Log.d("DeG", sba.toString());
        Log.d("DeG2", devlist.toString());

        if (!devs_to_populate.isEmpty()) devs_to_populate.clear();

        int i = 0;

        for (String temp : devlist) {
            if (sba.get(i++)) devs_to_populate.add(temp);
        }

        mMap.clear();
        markers.clear();

        populateMarkersForDevicesInDevs_To_PopulateList();
    }

    private float getMarkerColor(String color) {
        if (color.equalsIgnoreCase("RED")) return BitmapDescriptorFactory.HUE_RED;
        if (color.equalsIgnoreCase("BLUE")) return BitmapDescriptorFactory.HUE_BLUE;
        if (color.equalsIgnoreCase("GREEN")) return BitmapDescriptorFactory.HUE_GREEN;
        if (color.equalsIgnoreCase("ORANGE")) return BitmapDescriptorFactory.HUE_ORANGE;
        if (color.equalsIgnoreCase("VIOLET")) return BitmapDescriptorFactory.HUE_VIOLET;
        if (color.equalsIgnoreCase("ROSE")) return BitmapDescriptorFactory.HUE_ROSE;
        if (color.equalsIgnoreCase("MAGENTA")) return BitmapDescriptorFactory.HUE_MAGENTA;
        if (color.equalsIgnoreCase("AZURE")) return BitmapDescriptorFactory.HUE_AZURE;
        return BitmapDescriptorFactory.HUE_YELLOW;
    }

    private boolean settingsAllowGeofencesToBeShownOnWorldMap() {
        final SharedPreferences aqua_shared_prefs = this.getSharedPreferences("aqua_shared_prefs", this.MODE_PRIVATE);
        boolean showGeos = aqua_shared_prefs.getBoolean("!set_showGeos", false);

        if (showGeos) return true;

        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainMap Page", // TODO: Define a title for the content shown.
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
                "MainMap Page", // TODO: Define a title for the content shown.
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
}
