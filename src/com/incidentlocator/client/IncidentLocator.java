package com.incidentlocator.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import android.provider.Settings;
import android.util.Log;


public class IncidentLocator extends Activity
{
    private static final String TAG = "IncidentLocator";

    protected LocationManager locationManager;
    protected final LocationListener locationListener = new GetLocationListener();

    private static Location location;

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

        // check if GPS is enabled
        boolean isGpsEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (! isGpsEnabled) {
            // create a new dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                .setTitle("GPS is disabled")
                .setMessage("Do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // intent to open settings
                        Intent settings = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settings);
                    }
                });
            AlertDialog alert = builder.create();
            alert.show();
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0, locationListener);
   }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    public class GetLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            location = loc;
            Log.d(TAG, "[GPS]" + location.toString());
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
