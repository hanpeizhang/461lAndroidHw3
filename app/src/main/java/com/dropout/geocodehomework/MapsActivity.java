package com.dropout.geocodehomework;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public final static String EXTRA_MESSAGE = "com.dropout.geocodehomework.MESSAGE";
    private String currentMessage = "";
    private EditText editText;
    private ArrayList<LatLng> points = new ArrayList<>();
    private LatLng recPoint;
    /** Called when the user clicks the Send button */
    public void sendMessage(View view){
        // Do something in response to button
        editText = (EditText) findViewById(R.id.edit_message);;
        String message = editText.getText().toString();
        currentMessage = message;
        String inpAddress = message.replaceAll("\\s+", "+");

        String apikey = "AIzaSyAQ5f2diIU7PJEB6C4qxLoqCyFh7Cx3sJc";                                                                      //API KEY!!!!!!!!!!!!!!!!!!!!!
        String urlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=" + inpAddress + "&key=" + apikey;  //get youself an API key, it wont work without one
        Log.d("geo", urlStr);
        String jsonStr = "";
        HttpURLConnection conn = null;
        try {                                                                         ///WHY U NO HAVE API KEY!!!!!!!!!!!!!!
            URL url = new URL(urlStr);
            new GetJsonFromURL().execute(url);
        } catch (IOException e) {
            Log.d("geo", e.toString());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
       // mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoutbearr)));
    }
    private void addMarkerLoc(double lat, double lng, String name) {
        PolylineOptions opts;
        LatLng currentPoint = new LatLng(lat, lng);
//        for (LatLng point:points){
//           opts = new PolylineOptions();
//           opts.add(point,currentPoint);
//           mMap.addPolyline(opts);
//        }
        if (recPoint != null){
            opts = new PolylineOptions();
            opts.add(recPoint,currentPoint);
            opts.geodesic(true);
            opts.width(10);
            double dist = calcDistance(lat,lng);
            Log.i("color","/n"+dist);
            float adjDist = 1-(float)dist/20000;
            Log.i("color","/n"+adjDist);
            float[] hsv = new float[3];
            hsv[0] = 0;
            hsv[1] = 1;
            hsv[2] = adjDist;
            Log.i("color","/n"+hsv[2]);
            int color = Color.HSVToColor(hsv);
            opts.color(color);
            mMap.addPolyline(opts);
        }
        points.add(currentPoint);
        recPoint = currentPoint;
        CameraUpdate cUpFac = CameraUpdateFactory.newLatLng(currentPoint);
        mMap.moveCamera(cUpFac);
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Type: "+name+ "||Entry: "+currentMessage).icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoutbearicon)));
    }
    //distance calculations found at http://stackoverflow.com/questions/27928/how-do-i-calculate-distance-between-two-latitude-longitude-points
    private double calcDistance(double lat, double lng){
            double radius = 6378.1;
            double dLat = (recPoint.latitude-lat)*Math.PI/180;
            double dLng = (recPoint.longitude-lng)*Math.PI/180;
            double a = Math.sin(dLat/2)*Math.sin(dLat/2)+Math.cos(lat*Math.PI/180)*Math.cos(recPoint.latitude*Math.PI/180)*Math.sin(dLng/2)*Math.sin(dLng/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = radius * c;
            return d;
    }
    //Most of the Json parsing found at http://stackoverflow.com/questions/15711499/get-latitude-and-longitude-with-geocoder-and-android-google-maps-api-v2
    private void printJson(String json) {
        double lng=-1;
        double lat=-1;
        String name = "";
        JSONObject jsonObject = new JSONObject();
        boolean working = false;
        try {
            jsonObject = new JSONObject(json);
            String status = jsonObject.get("status").toString();
            if (status.equalsIgnoreCase("OK")){
                working = true;
            }
            name = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONArray("address_components").getJSONObject(0).getJSONArray("types").getString(0);
            lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            Log.d("latitude", "" + lat);
            Log.d("longitude", "" + lng);
        } catch (JSONException e) {
            Log.d("geo", e.toString());
        }
        String newName = name.replaceAll("_"," ");
        if (working) {
            MapsActivity.this.addMarkerLoc(lat, lng, newName);
            editText.setText("");
        }else{
            editText.setText("Not Found");
        }
//        TextView textView = new TextView(this);
//        textView.setTextSize(40);
//        Log.d("geo", "final" + json);
//        textView.setText("Latitiude: " + lat+"\nLongitude: "+lng);
//        setContentView(textView);
    }
    class GetJsonFromURL extends AsyncTask<URL, Integer, String> {
        protected String doInBackground(URL... inUrl) {
            HttpURLConnection conn = null;
            String jsonStr = "";
            try {
                URL url = inUrl[0];
                conn = (HttpURLConnection) url.openConnection();
                Log.i("Connection Status", conn.getResponseMessage());
            } catch (IOException e) {
                Log.d("geo", e.getMessage());
            }

            try {
                conn.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String current;
                while ((current = reader.readLine()) != null) {
                    jsonStr += current;
                    Log.d("geo", "current" + current);
                }
            } catch (Exception e) {
                Log.d("geo", e.toString());
            }
            Log.d("geo", "final" + jsonStr);
            return jsonStr;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(String result) {
            MapsActivity.this.printJson(result);
        }
    }
}
