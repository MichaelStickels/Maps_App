package com.example.stickelsm3342.maps_app;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    private LocationManager locationmanager;
    private Location myLocation;
    private static final int MY_LOC_ZOOM_FACTOR = 15;




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
        mMap.setMyLocationEnabled(true);

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

    public void myLocation(View v) {
    }


    public void getLocation() {
    }

    android.location.LocationListener locationListenerGps = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            dropMarker(location.getProvider());

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            //when network change from gps to wifi
            //

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    android.location.LocationListener locationListenerNetwork = new android.location.LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };



    public void dropMarker(String provider) {

        Log.d("message", provider);

        if (locationmanager != null)
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
            myLocation = locationmanager.getLastKnownLocation(provider);

        if(myLocation == null) {
            //display message
        } else {

            //get user location
            userlocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            //add marker
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(userlocation, MY_LOC_ZOOM_FACTOR);

            //drop the marker
            Circle circle = mMap.addCircle(new CircleOptions().center(userlocation).radius(1).strokeColor(Color.RED).strokeWidth(2).fillColor(Color.RED));

            mMap.animateCamera(update);

        }

    }
}
