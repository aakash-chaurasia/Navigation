package aakash.example.com.navigation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private LatLng SOURCE = new LatLng(40.722543, -73.998585);
    private LatLng DESTINATION = new LatLng(40.7057, -73.9964);
    private NavigationHelper navigationHelper;
    private BitmapDescriptor SOURCE_BITMAP;
    private BitmapDescriptor DESTINATION_BITMAP;
    private int ZOOM_LEVEL = 15;
    private int MIN_ZOOM_LEVEL = 10;
    private int MAP_ANGLE = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        navigationHelper = new NavigationHelper();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        SOURCE_BITMAP = createMarkersIcon(R.drawable.boy);
        DESTINATION_BITMAP = createMarkersIcon(R.drawable.treasure);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(MIN_ZOOM_LEVEL);
        if (SOURCE != null && DESTINATION != null) {
            navigationHelper.plotMap(mMap, SOURCE, DESTINATION);
            CameraUpdate cameraUpdate = getCameraPosition();
            mMap.moveCamera(cameraUpdate);
            addMarkers();
        } else {
            Toast.makeText(this, "Unable To Create Map", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private BitmapDescriptor createMarkersIcon(int drawable) {
        int height = 200;
        int width = 200;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(drawable);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    private void addMarkers() {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().icon(SOURCE_BITMAP).position(SOURCE)
                    .title("Source"));
            mMap.addMarker(new MarkerOptions().position(DESTINATION)
                    .title("Destination").icon(DESTINATION_BITMAP));
        }
    }

    private CameraUpdate getCameraPosition() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(SOURCE) // Sets the center of the map to
                .zoom(ZOOM_LEVEL)                   // Sets the zoom
                .tilt(MAP_ANGLE)    // Sets the tilt of the camera to 30 degrees
                .build();
        return CameraUpdateFactory.newCameraPosition(cameraPosition);
    }
}
