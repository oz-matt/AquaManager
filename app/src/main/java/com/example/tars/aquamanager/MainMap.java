package com.example.tars.aquamanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

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

        mMap.setInfoWindowAdapter(this);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            if (marker.getTitle().contains("@")) {
                String[] full_marker_data = marker.getTitle().split("@");

                String aqsen_pref = full_marker_data[4];
                String aqsen_ele = full_marker_data[5];

                Log.d("Mydata7", aqsen_pref);
                Log.d("Mydata8", aqsen_ele);

                Intent intent = new Intent(mContext, ViewRawData.class);
                intent.putExtra("aqsens_pref", aqsen_pref);
                intent.putExtra("aqsens_ele", Integer.parseInt(aqsen_ele));
                mContext.startActivity(intent);
            }
            }
        });

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
                Double rad = (Math.sqrt(Math.sqrt(Double.parseDouble(settings_obj.getString("area")))))*2;

                addSingleGeofenceToGeos_To_PopulateList(geo);
                populateMarkersForGeofencesInGeos_To_PopulateList();
                ZoomToFitMarkers(rad.floatValue());
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

                    final List<String> temp_array_of_geos = new ArrayList<>();
                    final List<String> temp_array_of_geos_fake = new ArrayList<>();
                    List<Boolean> temp_array_of_geo_booleans = new ArrayList<>();

                    int num_devs = 0;

                    for (java.util.Map.Entry<String, ?> entry : keys.entrySet()) {
                        String key = entry.getKey();
                        if (key.endsWith("qdata")) {
                            String name = key.substring(5, key.length() - 6);
                            temp_array_of_devs.add(name);

                            num_devs++;

                            boolean is_in_devs_to_populate = false;

                            for (String temp : devs_to_populate) {
                                if (temp.equalsIgnoreCase(name)) {
                                    is_in_devs_to_populate = true;
                                }
                            }
                            temp_array_of_dev_booleans.add(is_in_devs_to_populate);
                        } else if (key.startsWith("!geo_") && key.endsWith("_settings")) {
                            String geo_name = key.substring(5, key.length() - 9);
                            temp_array_of_geos.add(geo_name);
                            temp_array_of_geos_fake.add(geo_name + " (Geofence)");

                            boolean is_in_geos_to_populate = false;

                            for (String temp : geos_to_populate) {
                                if (temp.equalsIgnoreCase(geo_name)) {
                                    is_in_geos_to_populate = true;
                                }
                            }
                            temp_array_of_geo_booleans.add(is_in_geos_to_populate);
                        }
                    }

                    final List<String> temp_array_of_all = new ArrayList<>();
                    final List<String> temp_array_of_all_fake = new ArrayList<>();
                    List<Boolean> temp_array_of_all_booleans = new ArrayList<>();

                    temp_array_of_all.addAll(temp_array_of_devs);
                    temp_array_of_all.addAll(temp_array_of_geos);
                    temp_array_of_all_fake.addAll(temp_array_of_devs);
                    temp_array_of_all_fake.addAll(temp_array_of_geos_fake);
                    temp_array_of_all_booleans.addAll(temp_array_of_dev_booleans);
                    temp_array_of_all_booleans.addAll(temp_array_of_geo_booleans);

                    CharSequence[] items = temp_array_of_all_fake.toArray(new CharSequence[0]);
                    boolean[] isChecked = new boolean[temp_array_of_all_booleans.size()];
                    for (int i = 0; i < temp_array_of_all_booleans.size(); i++) {
                        isChecked[i] = temp_array_of_all_booleans.get(i);
                    }
                    final ArrayList selectedItems = new ArrayList();

                    if (temp_array_of_all.isEmpty()) {
                        AlertDialog dialog = new AlertDialog.Builder(mContext)
                                .setTitle("No Devices Or Geofences To Add")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create();
                        dialog.show();
                    } else {

                        final int nd = num_devs;

                        dialog = new AlertDialog.Builder(mContext)
                                .setTitle("Select Devices And Geofences To Display")
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
                                        getCheckedNamesAndRepopulateBothDevsAndGeos_To_Populate(temp_array_of_all, nd);

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

                int num_markers = aqua_shared_prefs.getInt("!set_numberOfMarkersToShow", 50);
                Log.d("num6", String.valueOf(num_markers));

                if (aqsens_json_array.length() < num_markers) num_markers = aqsens_json_array.length();
                Log.d("num6", String.valueOf(num_markers));

                for (int i = 0; i < num_markers; i++) {
                    JSONObject this_aqsens_element = aqsens_json_array.getJSONObject(i);
                    JSONObject this_elements_gpsminimum = this_aqsens_element.getJSONObject("gpsminimum");
                    JSONObject this_elements_sensors = this_aqsens_element.getJSONObject("sensors");
                    Double lat = Double.parseDouble(this_elements_gpsminimum.getString("lat"));
                    Double lon = Double.parseDouble(this_elements_gpsminimum.getString("lon"));

                    String datetime = this_elements_gpsminimum.getString("time");
                    String speed = this_elements_gpsminimum.getString("gspeed");
                    String numsat = this_elements_gpsminimum.getString("numsat");
                    String batt = this_elements_sensors.getString("pct_battery");

                    String aqsens_pref = "!dev_" + name + "_aqsens";
                    String aqsens_ele = String.valueOf(i);

                    String full_marker_data = datetime + "@" + speed + "@" + numsat + "@" + batt + "@" + aqsens_pref + "@" + aqsens_ele;

                    LatLng markerSpot = new LatLng(lat, lon);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerSpot).title(full_marker_data).snippet("Going x mph").icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(this_devices_marker_color))));
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
                            .fillColor(0x4a65a3da)
                            .strokeColor(0xe22f4253)
                            .strokeWidth(4);

                    mMap.addCircle(circleOptions);

                    LatLng markerSpot = new LatLng(lat, lon);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerSpot).title(name).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("flagd", (int) convertDpToPixel(70, this), (int) convertDpToPixel(49, this)))));

                    markers.add(marker);
                } else if (geotype_str.equalsIgnoreCase("polygon")) {

                    Polygon polygon;
                    List<LatLng> box_latlngs = new ArrayList<>();

                    JSONArray latlng_array = new JSONArray(settings_obj.getString("geodata"));

                    for (int i = 0; i<latlng_array.length(); i++) {
                        JSONArray latlng_inner_arr = latlng_array.getJSONArray(i);
                        String lat = latlng_inner_arr.getString(0);
                        String lon = latlng_inner_arr.getString(1);
                        LatLng ll = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                        box_latlngs.add(i, ll);
                    }

                    PolygonOptions rectOptions = new PolygonOptions().addAll(box_latlngs);

                    polygon = mMap.addPolygon(rectOptions);
                    polygon.setStrokeColor(0xe22f4253);
                    polygon.setFillColor(0x4a65a3da);
                    polygon.setStrokeWidth(4);

                    Double lat = Double.parseDouble(settings_obj.getString("lat"));
                    Double lon = Double.parseDouble(settings_obj.getString("lon"));

                    LatLng markerSpot = new LatLng(lat, lon);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerSpot).title(name).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("flagd", (int) convertDpToPixel(70, this), (int) convertDpToPixel(49, this)))));

                    markers.add(marker);
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

    private void getCheckedNamesAndRepopulateBothDevsAndGeos_To_Populate(List<String> devlist, int total_num_devs) {

        SparseBooleanArray sba = dialog.getListView().getCheckedItemPositions();

        if (!devs_to_populate.isEmpty()) devs_to_populate.clear();
        if (!geos_to_populate.isEmpty()) geos_to_populate.clear();

        int i = 0;

        for (String temp : devlist) {
            if (sba.get(i)) {
                if (i < (total_num_devs)) {
                    devs_to_populate.add(temp);
                } else {
                    //geos_to_populate.add(temp.substring(0,temp.length()-11));
                    geos_to_populate.add(temp);
                }
            }
            i++;
        }

        mMap.clear();
        markers.clear();

        populateMarkersForDevicesInDevs_To_PopulateList();
        populateMarkersForGeofencesInGeos_To_PopulateList();
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

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
        //return prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        return prepareInfoView(marker);

    }

    private View prepareInfoView(Marker marker){

        String title = marker.getTitle();

        if (title.contains("@")) {

            //prepare InfoView programmatically
            LinearLayout infoView = new LinearLayout(MainMap.this);
            LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            infoView.setOrientation(LinearLayout.HORIZONTAL);
            infoView.setLayoutParams(infoViewParams);

            ImageButton infoImageView = new ImageButton(MainMap.this);
            //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            //btnParams.addRule(RelativeLayout.CENTER_VERTICAL);
            infoImageView.setLayoutParams(btnParams);
            infoImageView.setBackgroundColor(getResources().getColor(R.color.white));
            Drawable drawable = getResources().getDrawable(R.drawable.paper_blu);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, (int) AquaUtil.convertDpToPixel(35, mContext), (int) AquaUtil.convertDpToPixel(35, mContext), true));
            infoImageView.setImageDrawable(d);
            infoImageView.setPadding(0, 0, 8, 0);


            infoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "click!", Toast.LENGTH_SHORT).show();
                }
            });

            RelativeLayout rl = new RelativeLayout(MainMap.this);
            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
            rl.setLayoutParams(rlParams);

            rl.addView(infoImageView);

            infoView.addView(rl);

            RelativeLayout vw = new RelativeLayout(MainMap.this);
            //vw.setGravity(Gravity.CENTER_VERTICAL);
            vw.setBackgroundColor(getResources().getColor(R.color.holoBlueDark));
            RelativeLayout.LayoutParams row_lp = new RelativeLayout.LayoutParams(4, RelativeLayout.LayoutParams.MATCH_PARENT);
            row_lp.addRule(RelativeLayout.CENTER_VERTICAL);
            vw.setPadding(0, 7, 4, 0);
            infoView.addView(vw, row_lp);

            LinearLayout subInfoView = new LinearLayout(MainMap.this);
            LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            subInfoView.setOrientation(LinearLayout.VERTICAL);
            subInfoView.setLayoutParams(subInfoViewParams);

            String[] full_marker_data = marker.getTitle().split("@");

            String datetime_str_un = full_marker_data[0];
            String datetime_str_editted = datetime_str_un.split("\\.")[0] + "Z";
            String speed = full_marker_data[1];
            String numsat = full_marker_data[2];
            String batt = full_marker_data[3];

            SimpleDateFormat dfp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dfp.setTimeZone(TimeZone.getTimeZone("GMT"));

            String try_time_str = "<no data>";

            Log.d("Mydata4", marker.getTitle());
            Log.d("Mydata1", datetime_str_un);
            Log.d("Mydata", datetime_str_editted);

            try {
                Date formatted_date = dfp.parse(datetime_str_editted);
                try_time_str = formatted_date.toString();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Date Parse Error 102", Toast.LENGTH_SHORT).show();
            }
            TextView subInfo1 = new TextView(MainMap.this);
            subInfo1.setText(try_time_str);
            subInfo1.setTextColor(getResources().getColor(R.color.holoBlueDark));
            TextView subInfo2 = new TextView(MainMap.this);
            subInfo2.setText("Going " + speed + "mph");
            subInfo2.setTextColor(getResources().getColor(R.color.holoBlueDark));
            subInfo2.setPadding(8, 0, 0, 0);
            subInfo1.setPadding(8, 0, 0, 0);

            TextView subInfo3 = new TextView(MainMap.this);
            subInfo3.setText(numsat + " satellites in view");
            subInfo3.setTextColor(getResources().getColor(R.color.holoBlueDark));
            subInfo3.setPadding(8, 0, 0, 0);

            TextView subInfo4 = new TextView(MainMap.this);
            subInfo4.setText(batt + "% battery");
            subInfo4.setTextColor(getResources().getColor(R.color.holoBlueDark));
            subInfo4.setPadding(8, 0, 0, 0);

            subInfoView.addView(subInfo1);
            subInfoView.addView(subInfo2);
            subInfoView.addView(subInfo3);
            subInfoView.addView(subInfo4);

            infoView.addView(subInfoView);

            return infoView;

        } else {
            TextView geotv = new TextView(MainMap.this);
            String cat_geo_title = "Geofence: '" + title + "'";
            geotv.setText(cat_geo_title);
            geotv.setTextColor(getResources().getColor(R.color.holoBlueDark));

            return geotv;
        }
    }

}
