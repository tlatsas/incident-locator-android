package com.incidentlocator.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import android.content.Context;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;

import android.util.Log;


public class IncidentLocator extends Activity
{
    private static final String TAG = "IncidentLocator";

    protected LocationManager locationManager;

    private static float lat;
    private static float lng;

    private EditText coordinatesBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        coordinatesBox = (EditText) findViewById(R.id.show_message);

        locationManager =
            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO: request both gps and network updates
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0, new GetLocationListener());
    }

    public class GetLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            lat = (float) loc.getLatitude();
            lng = (float) loc.getLongitude();
            Log.d(TAG, "[GPS] lat: " + lat);
            Log.d(TAG, "[GPS] lng: " + lng);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }

    public void getLocation(View view) {
        String message = "Lat: " + Float.toString(lat) +
                         " Lng: " + Float.toString(lng) + "\n";
        coordinatesBox.append(message);
    }
}
