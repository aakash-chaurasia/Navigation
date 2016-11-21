package aakash.example.com.navigation;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng SOURCE = new LatLng(33.424564, -111.94);
    private LatLng DESTINATION = new LatLng(40.7057, -73.9964);
    private NavigationHelper navigationHelper;
    private BitmapDescriptor SOURCE_BITMAP;
    private BitmapDescriptor DESTINATION_BITMAP;
    private int ZOOM_LEVEL = 15;
    private int MIN_ZOOM_LEVEL = 10;
    private int MAP_ANGLE = 45;
    private Marker sourceMarker;
    private String provStr = LocationManager.GPS_PROVIDER;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        navigationHelper = new NavigationHelper();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        locationListener = new myLocationListener();
        location = getCurrentLocation();
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
    public void onMapReady(GoogleMap googlemap) {
        mMap = googlemap;

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            SOURCE = latLng;
        } else {
            Toast.makeText(this, "Please enable gps", Toast.LENGTH_LONG).show();
            DESTINATION = null;
        }
        mMap.setMinZoomPreference(MIN_ZOOM_LEVEL);
        if (SOURCE != null && DESTINATION != null) {
            navigationHelper.plotMap(mMap, SOURCE, DESTINATION);
            addMarkers();
        }
        CameraUpdate cameraUpdate = getCameraPosition();
        mMap.moveCamera(cameraUpdate);
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
            sourceMarker = mMap.addMarker(new MarkerOptions().icon(SOURCE_BITMAP).position(SOURCE)
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

    private Location getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1 );
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location location = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (location == null || l.getAccuracy() < location.getAccuracy()) {
                location = l;
            }
        }
        locationManager.requestLocationUpdates(provStr, 10, 0, locationListener);
        return location;
    }

    class myLocationListener implements LocationListener {
        private String tag = "me";
        @Override
        public void onLocationChanged(Location location) {
            SOURCE = new LatLng(location.getLatitude(), location.getLongitude());
            sourceMarker.setPosition(SOURCE);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
