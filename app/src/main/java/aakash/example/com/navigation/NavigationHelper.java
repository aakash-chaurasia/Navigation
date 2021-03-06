package aakash.example.com.navigation;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aakash on 11/20/2016.
 */

public class NavigationHelper {
    private GoogleMap mMap;
    private int COLOR = Color.BLUE;
    public void plotMap(GoogleMap mMap, LatLng SOURCE, LatLng DESTINATION) {
        this.mMap = mMap;
        String url = getDirectionApiURL(SOURCE, DESTINATION);
        GetJSONRoutes downloadTask = new GetJSONRoutes();
        downloadTask.execute(url);
    }

    public String getDirectionApiURL(LatLng source, LatLng destination) {
        String originStr = "origin="+ source.latitude + "," + source.longitude;
        String destinationStr = "destination="+ destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String params = originStr + "&" + destinationStr + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;
        return url;
    }

    public LatLng getLatLngFromAddress(Context context,String strAddress) {

        Geocoder geocoder = new Geocoder(context);
        Address address;
        LatLng latLng = null;

        try {
            address = geocoder.getFromLocationName(strAddress, 5).get(0);
            if (address == null) {
                return null;
            }
            latLng = new LatLng(address.getLatitude(), address.getLongitude() );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return latLng;
    }

    class GetJSONRoutes extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... googleUrl) {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(googleUrl[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
                try {
                    iStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                urlConnection.disconnect();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParseJson().execute(result);
        }
    }

     class ParseJson extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(30);
                polyLineOptions.color(COLOR);
            }
            mMap.addPolyline(polyLineOptions);
        }
    }
}
