package uk.co.findmyride.track;

import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import android.location.Location;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;

public class MapsActivity extends FragmentActivity {

    /*
    This activity displays the locations to the user.
     */
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    // All the location settings will be here
    private String dName = "";
    private String dTime = "";
    private double dLat = 0.0;
    private double dLng = 0.0;
    private double dSpeed = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Enables strict mode
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String message = "";
        // Trying to get extras from the intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            getLocationDetails(extras);
            //setUpMapIfNeeded(message);
        }

    }

    public void getLocationDetails(Bundle extraDetails) {
        dName = extraDetails.getString("deviceName");
        dTime = extraDetails.getString("deviceTime");
        dLat = extraDetails.getDouble("deviceLat");
        dLng = extraDetails.getDouble("deviceLng");
        dSpeed = extraDetails.getDouble("deviceSpeed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded(new String(""));
    }

    private void setUpMapIfNeeded(String message) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(message);
            }
        }
    }

    public LatLng getLocation() {
        /**
         *  Returns the current location of the user
         */
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(String message) {
        // sets the title and the position of the marker


        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                addMyPos(location);
                addCarPos();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    public void addMyPos(Location myLoc) {

        mMap.clear();
        LatLng myPos = new LatLng(myLoc.getLatitude(), myLoc.getLongitude());
        MarkerOptions myPosOpt = new MarkerOptions().position(myPos).title("You are here!");
        mMap.addMarker(myPosOpt);

    }

    public void addCarPos() {
        String deviceDisplayStr = "(" + dName + ") Time: " + dTime + " Speed: " + dSpeed;
        LatLng curPos = new LatLng(dLat, dLng);

        mMap.addMarker(new MarkerOptions().position(curPos).title(deviceDisplayStr));
        if (mMap.getCameraPosition().zoom < 16) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 16.0f));
        }
    }

}
