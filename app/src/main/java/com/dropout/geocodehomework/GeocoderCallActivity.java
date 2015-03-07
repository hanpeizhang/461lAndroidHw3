package com.dropout.geocodehomework;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class GeocoderCallActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MapsActivity.EXTRA_MESSAGE);
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

    public void printJson(String json) {
        double lng=-1;
        double lat=-1;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(json);

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
//        TextView textView = new TextView(this);
//        textView.setTextSize(40);
//        Log.d("geo", "final" + json);
//        textView.setText("Latitiude: " + lat+"\nLongitude: "+lng);
//        setContentView(textView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geocoder_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            GeocoderCallActivity.this.printJson(result);
        }
    }
}