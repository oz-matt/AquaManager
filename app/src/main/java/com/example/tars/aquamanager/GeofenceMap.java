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
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GeofenceMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,GoogleMap.OnMapClickListener,GoogleMap.OnMarkerDragListener {

        private static GoogleMap mMap;
        private static Marker flagmarker = null;

        private static Circle circle = null;
        private static Polygon polygon = null;
        private static LatLng geocenter = null;

        private static List<String> marker_ids = new ArrayList<>();
        private static List<LatLng> box_latlngs = new ArrayList<>();

        private static List<Double> pointX = new ArrayList<>();
        private static List<Double> pointY = new ArrayList<>();

        private static Context context;

        private static Resources static_resources;
        private static String pkg_name;

        private static LinearLayout tab_ll;

        private static HttpURLConnection urlConnection;
        private static Activity activity;

        public static boolean longClickEnable = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_geofence_map);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.geofencemap);
            mapFragment.getMapAsync(this);

            longClickEnable = true;

            activity = this;

            context = getApplicationContext();
            static_resources = getResources();
            pkg_name = getPackageName();

            ImageButton zoom_in = (ImageButton) findViewById(R.id.zoomin_geo);
            ImageButton zoom_out = (ImageButton) findViewById(R.id.zoomout_geo);

            final RelativeLayout zoom_in_rl = (RelativeLayout) findViewById(R.id.zoomin_rl_geo);
            final RelativeLayout zoom_out_rl = (RelativeLayout) findViewById(R.id.zoomout_rl_geo);

            tab_ll = (LinearLayout) findViewById(R.id.geo_tab_ll);

            zoom_in.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
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
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        zoom_out_rl.setBackgroundResource(R.color.colorAqua);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        zoom_out_rl.setBackgroundResource(R.color.whiteTransparent);
                        mMap.animateCamera(CameraUpdateFactory.zoomOut());
                    }
                    return true;
                }
            });

        }

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

            mMap.setOnMarkerDragListener(this);

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng point) {
                    if (longClickEnable) {
                        geocenter = point;
                        Intent intent = new Intent(getApplicationContext(), NewGeoInfo.class);
                        startActivity(intent);
                    }
                }
            });
        }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    private static Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(static_resources,static_resources.getIdentifier(iconName, "drawable", pkg_name));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public static void geofenceAddCircleAndName (final Double rad, final String name) {

        longClickEnable = false;

        CircleOptions circleOptions = new CircleOptions()
                .center(geocenter)
                .radius(rad * 1609.34)
                .fillColor(0x4a65a3da)
                .strokeColor(0xe22f4253)
                .strokeWidth(4);

        mMap.clear();

        MarkerOptions markeroptions = new MarkerOptions().position(geocenter)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("flagd",(int) convertDpToPixel(70, context),(int) convertDpToPixel(49, context))))
                .title(name);
        flagmarker = mMap.addMarker(markeroptions);
        flagmarker.showInfoWindow();

        circle = mMap.addCircle(circleOptions);

        tab_ll.removeAllViews();

        TextView isgeofencegood = new TextView(context);
        RelativeLayout.LayoutParams tv_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        isgeofencegood.setText("Save this geofence?");
        isgeofencegood.setLayoutParams(tv_lp);
        isgeofencegood.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);

        RelativeLayout holder = new RelativeLayout(context);
        LinearLayout.LayoutParams holder_lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);
        holder.setLayoutParams(holder_lp);
        holder.addView(isgeofencegood);

        Button cancel_btn = new Button(context);
        LinearLayout.LayoutParams redo_lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.25f);
        cancel_btn.setText("Cancel");
        cancel_btn.setLayoutParams(redo_lp);

        Button save_btn = new Button(context);
        LinearLayout.LayoutParams confirm_lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.25f);
        save_btn.setText("Save");
        save_btn.setLayoutParams(confirm_lp);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longClickEnable = true;
                tab_ll.removeAllViews();
                mMap.clear();
                RelativeLayout pressnhold_rl = new RelativeLayout(context);
                RelativeLayout.LayoutParams pnh_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                pressnhold_rl.setLayoutParams(pnh_lp);

                TextView pressnhold_tv = new TextView(context);
                RelativeLayout.LayoutParams pnhtv_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                pnhtv_lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                pressnhold_tv.setText("Press and hold to add geofence");
                pressnhold_tv.setLayoutParams(pnhtv_lp);
                pressnhold_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
                pressnhold_rl.addView(pressnhold_tv);

                tab_ll.addView(pressnhold_rl);
                flagmarker = null;
                circle = null;
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String lat = String.valueOf(geocenter.latitude);
                String lon = String.valueOf(geocenter.longitude);

                Double area = 3.1416 * (rad * rad);

                String[] getlocdata = {lat, lon, name, "circle", rad.toString(), area.toString()};

                GetLocationOnly get = new GetLocationOnly(activity);
                get.execute(getlocdata);

                //activity.finish();
            }
        });

        tab_ll.addView(holder);
        tab_ll.addView(cancel_btn);
        tab_ll.addView(save_btn);

    }

    public static void geofenceAddPolygonAndName (final Double rad, final String name) {
        mMap.clear();

        MarkerOptions markeroptions = new MarkerOptions().position(geocenter)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("flagd",(int) convertDpToPixel(70, context),(int) convertDpToPixel(49, context))))
                .title(name);
        flagmarker = mMap.addMarker(markeroptions);
        flagmarker.showInfoWindow();

        double box[] = getBoundingBox(geocenter.latitude, geocenter.longitude, rad * 1609.34);

        longClickEnable = false;

        double d_pointX[] = {box[0], box[0], box[0], geocenter.latitude, box[2], box[2], box[2], geocenter.latitude};
        double d_pointY[] = {box[1], geocenter.longitude, box[3], box[3], box[3], geocenter.longitude, box[1], box[1]};

        for (int i = 0; i<8; i++) {
            pointX.add(i, d_pointX[i]);
            pointY.add(i, d_pointY[i]);
        }

        for (int i = 0 ; i < 8; i++){
            MarkerOptions boxMarkerOptions = new MarkerOptions().position(new LatLng(pointX.get(i), pointY.get(i)))
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("orb",(int) convertDpToPixel(30, context),(int) convertDpToPixel(30, context))));
            Marker marker = mMap.addMarker(boxMarkerOptions);
            marker.setAnchor(0.5f, 0.5f);
            marker.setDraggable(true);
            marker_ids.add(i, marker.getId());
        }

        box_latlngs.clear();

        for (int i = 0 ; i < 8; i++) {
            box_latlngs.add(new LatLng(pointX.get(i), pointY.get(i)));
        }

        PolygonOptions rectOptions = new PolygonOptions().addAll(box_latlngs);

        polygon = mMap.addPolygon(rectOptions);
        polygon.setStrokeColor(0xe22f4253);
        polygon.setStrokeWidth(4);
        polygon.setFillColor(0x4a65a3da);
        tab_ll.removeAllViews();

        TextView isgeofencegood = new TextView(context);
        RelativeLayout.LayoutParams tv_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        isgeofencegood.setText("Save this geofence?");
        isgeofencegood.setLayoutParams(tv_lp);
        isgeofencegood.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);

        RelativeLayout holder = new RelativeLayout(context);
        LinearLayout.LayoutParams holder_lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);
        holder.setLayoutParams(holder_lp);
        holder.addView(isgeofencegood);

        Button cancel_btn = new Button(context);
        LinearLayout.LayoutParams redo_lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.25f);
        cancel_btn.setText("Cancel");
        cancel_btn.setLayoutParams(redo_lp);

        Button save_btn = new Button(context);
        LinearLayout.LayoutParams confirm_lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.25f);
        save_btn.setText("Save");
        save_btn.setLayoutParams(confirm_lp);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab_ll.removeAllViews();
                mMap.clear();
                RelativeLayout pressnhold_rl = new RelativeLayout(context);
                RelativeLayout.LayoutParams pnh_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                pressnhold_rl.setLayoutParams(pnh_lp);

                TextView pressnhold_tv = new TextView(context);
                RelativeLayout.LayoutParams pnhtv_lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                pnhtv_lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                pressnhold_tv.setText("Press and hold to add geofence");
                pressnhold_tv.setLayoutParams(pnhtv_lp);
                pressnhold_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
                pressnhold_rl.addView(pressnhold_tv);

                longClickEnable = true;
                tab_ll.addView(pressnhold_rl);
                flagmarker = null;
                polygon = null;
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String lat = String.valueOf(flagmarker.getPosition().latitude);
                String lon = String.valueOf(flagmarker.getPosition().longitude);

                Double area_d = (calculateAreaOfGPSPolygonOnEarthInSquareMeters(box_latlngs))/(2.59e6);
                Integer area = area_d.intValue();

                try {

                    JSONArray box_json_array = new JSONArray();

                    for (int i = 0; i<8; i++) {
                        JSONObject lat_lang = new JSONObject();
                        lat_lang.put("lat", box_latlngs.get(i).latitude);
                        lat_lang.put("lon", box_latlngs.get(i).longitude);
                        box_json_array.put(i, lat_lang);
                    }

                    String[] getlocdata = {lat, lon, name, "polygon", box_json_array.toString(), area.toString()};

                    GetLocationOnly get = new GetLocationOnly(activity);
                    get.execute(getlocdata);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "JSON Exception 101", Toast.LENGTH_SHORT).show();
                }
                //activity.finish();
            }
        });

        tab_ll.addView(holder);
        tab_ll.addView(cancel_btn);
        tab_ll.addView(save_btn);

    }

    private static double[] getBoundingBox(final double pLatitude, final double pLongitude, final double pDistanceInMeters) {

        final double[] boundingBox = new double[4];

        final double latRadian = Math.toRadians(pLatitude);

        final double degLatKm = 110.574235;
        final double degLongKm = 110.572833 * Math.cos(latRadian);
        final double deltaLat = pDistanceInMeters / 1000.0 / degLatKm;
        final double deltaLong = pDistanceInMeters / 1000.0 /
                degLongKm;

        final double minLat = pLatitude - deltaLat;
        final double minLong = pLongitude - deltaLong;
        final double maxLat = pLatitude + deltaLat;
        final double maxLong = pLongitude + deltaLong;

        boundingBox[0] = minLat;
        boundingBox[1] = minLong;
        boundingBox[2] = maxLat;
        boundingBox[3] = maxLong;

        return boundingBox;
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
        //polygon.setFillColor(R.color.transparent);

    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub
        mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));
    }

    @Override
    public void onMapLongClick(LatLng arg0) {
        // TODO Auto-generated method stub

        //create new marker when user long clicks
        //mMap.addMarker(new MarkerOptions()
        //        .position(arg0)
        //        .draggable(true));
    }

    @Override
    public void onMarkerDragEnd(Marker arg0) {

        int current_marker_id = marker_ids.indexOf(arg0.getId());

        polygon.setFillColor(0x4a65a3da);

        for (int i = 0 ; i < 8; i++) {
            if (i == current_marker_id) {
                box_latlngs.set(i, arg0.getPosition());
            }
        }

        flagmarker.setPosition(computeCentroid(box_latlngs));

        polygon.setPoints(box_latlngs);
    }

    @Override
    public void onMarkerDrag(Marker arg0) {
        /*int current_marker_id = marker_ids.indexOf(arg0.getId());



        for (int i = 0 ; i < 8; i++) {
            if (i == current_marker_id) {
                box_latlngs.set(i, arg0.getPosition());
            }
        }

        //flagmarker.setPosition(computeCentroid(box_latlngs));

        polygon.setPoints(box_latlngs);*/

    }

    private LatLng computeCentroid(List<LatLng> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude/n, longitude/n);
    }

    private static final double EARTH_RADIUS = 6371000;// meters

    public static double calculateAreaOfGPSPolygonOnEarthInSquareMeters(final List<LatLng> locations) {
        return calculateAreaOfGPSPolygonOnSphereInSquareMeters(locations, EARTH_RADIUS);
    }

    private static double calculateAreaOfGPSPolygonOnSphereInSquareMeters(final List<LatLng> locations, final double radius) {
        if (locations.size() < 3) {
            return 0;
        }

        final double diameter = radius * 2;
        final double circumference = diameter * Math.PI;
        final List<Double> listY = new ArrayList<Double>();
        final List<Double> listX = new ArrayList<Double>();
        final List<Double> listArea = new ArrayList<Double>();
        // calculate segment x and y in degrees for each point
        final double latitudeRef = locations.get(0).latitude;
        final double longitudeRef = locations.get(0).longitude;
        for (int i = 1; i < locations.size(); i++) {
            final double latitude = locations.get(i).latitude;
            final double longitude = locations.get(i).longitude;
            listY.add(calculateYSegment(latitudeRef, latitude, circumference));
            //Log.d(LOG_TAG, String.format("Y %s: %s", listY.size() - 1, listY.get(listY.size() - 1)));
            listX.add(calculateXSegment(longitudeRef, longitude, latitude, circumference));
            //Log.d(LOG_TAG, String.format("X %s: %s", listX.size() - 1, listX.get(listX.size() - 1)));
        }

        // calculate areas for each triangle segment
        for (int i = 1; i < listX.size(); i++) {
            final double x1 = listX.get(i - 1);
            final double y1 = listY.get(i - 1);
            final double x2 = listX.get(i);
            final double y2 = listY.get(i);
            listArea.add(calculateAreaInSquareMeters(x1, x2, y1, y2));
            //Log.d(LOG_TAG, String.format("area %s: %s", listArea.size() - 1, listArea.get(listArea.size() - 1)));
        }

        // sum areas of all triangle segments
        double areasSum = 0;
        for (final Double area : listArea) {
            areasSum = areasSum + area;
        }

        // get abolute value of area, it can't be negative
        return Math.abs(areasSum);// Math.sqrt(areasSum * areasSum);
    }

    private static Double calculateAreaInSquareMeters(final double x1, final double x2, final double y1, final double y2) {
        return (y1 * x2 - x1 * y2) / 2;
    }

    private static double calculateYSegment(final double latitudeRef, final double latitude, final double circumference) {
        return (latitude - latitudeRef) * circumference / 360.0;
    }

    private static double calculateXSegment(final double longitudeRef, final double longitude, final double latitude,
                                            final double circumference) {
        return (longitude - longitudeRef) * circumference * Math.cos(Math.toRadians(latitude)) / 360.0;
    }

}
