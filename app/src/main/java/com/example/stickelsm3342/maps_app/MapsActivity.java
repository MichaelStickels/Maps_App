package com.example.stickelsm3342.maps_app;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    int toggle = 0;
    private LatLng userlocation = null;
    private LocationManager locationManager;
    private Location myLocation;
    private static final int MY_LOC_ZOOM_FACTOR = 15;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15 * 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;
    private int loctoggle = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        LatLng sandiego = new LatLng(32.7, -117);
        mMap.addMarker(new MarkerOptions().position(sandiego).title("Marker in San Diego"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sandiego));


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);

    }

    public void mapView(View v) {
        if (toggle == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            toggle = 1;
        } else if (toggle == 1) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            toggle = 0;
        }

    }

    public void trackbutton(View v) {

        if (loctoggle == 0) {
            tracking();
            loctoggle = 1;
        } else {
            stoptracking();
            loctoggle = 0;
        }

    }

    public void stoptracking() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(locationListenerGPS);
        locationManager.removeUpdates(locationListenerNetwork);
        locationManager = null;

    }

    public void tracking() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



            //get GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled)
                Log.d("MyMaps", "getLocation: GPS enabled");
            //get network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled)
                Log.d("MyMaps", "getLocation: Network enabled");

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d("MyMaps", "getLocation: No provider enabled");
            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {
                    Log.d("MyMaps", "getLocation: Network enabled, requesting location updates");


                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);


                    Log.d("MyMaps","getLocation: Network update request successful");
                    Toast.makeText(this, "Using Network", Toast.LENGTH_SHORT);
                }
                if(isGPSEnabled){
                    Log.d("MyMaps", "getLocation: GPS enabled, requesting location updates");
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,locationListenerGPS);

                    Log.d("MyMaps","getLocation: GPS update request successful");
                    Toast.makeText(this, "Using GPS", Toast.LENGTH_SHORT);
                }
            }

        } catch(Exception e){
            Log.d("MyMaps","Exception in getLocation" );
            e.printStackTrace();
        }

    }

    LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            //Log.d and Toast that GPS is enabled and working

            //Drop a marker
            dropGPSMarker(location.getProvider());
            //Remove the network location updates
            try{
                locationManager.removeUpdates(locationListenerNetwork);
            } catch(SecurityException e){

            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.d and Toast that GPS is enabled and working


            switch(status){
                case LocationProvider.AVAILABLE:
                    Log.d("Status", "Location Provider is Available");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("Status", "Location Provider isn't available");
                    try {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    }catch(SecurityException e){

                    }
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("Status","Location Provider is temporarily unavailable");
                    break;
                default:
                    try{
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
                    }catch(SecurityException e){

                    }
            }

        }
        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            //Log.d and Toast that GPS is enabled and working

            //Drop a marker on map
            dropNetworkMarker(location.getProvider());
            //Relaunch the network provider, request location Updates (NETWORK)
            try{
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerNetwork);
            }
            catch(SecurityException e) {

            }
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.d and Toast that GPS is enabled and working
            Log.d("Status","GPS is enabled and working");
        }
        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    public void dropGPSMarker(String provider){
        if(locationManager!=null){
            try{
                myLocation = locationManager.getLastKnownLocation(provider);
            }catch(SecurityException e){
            }

        }
        if(myLocation == null){
            //display a message
        }
        else{
            userlocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userlocation, MY_LOC_ZOOM_FACTOR);
            Circle marker = mMap.addCircle((new CircleOptions().center(userlocation)).radius(1).strokeColor(Color.GREEN).fillColor(Color.GREEN));
            mMap.animateCamera(update);
        }

    }
    public void dropNetworkMarker(String provider){
        if(locationManager!=null){
            try{
                myLocation = locationManager.getLastKnownLocation(provider);
            }catch(SecurityException e){
            }

        }
        if(myLocation == null){
            //display a message
        }
        else{
            userlocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userlocation, MY_LOC_ZOOM_FACTOR);
            Circle marker = mMap.addCircle((new CircleOptions().center(userlocation)).radius(1).strokeColor(Color.RED).fillColor(Color.RED));
            mMap.animateCamera(update);
        }

    }



//    public void dropMarker(String provider) {
//
//        Log.d("message", provider);
//
//        if (locationManager != null)
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            myLocation = locationManager.getLastKnownLocation(provider);
//
//        if(myLocation == null) {
//            //display message
//        } else {
//
//            //get user location
//            userlocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//
//            //add marker
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userlocation, MY_LOC_ZOOM_FACTOR);
//
//            //drop the marker
//            Circle circle = mMap.addCircle(new CircleOptions().center(userlocation).radius(1).strokeColor(Color.RED).strokeWidth(2).fillColor(Color.RED));
//
//            mMap.animateCamera(update);
//
//        }
//
//    }
}
