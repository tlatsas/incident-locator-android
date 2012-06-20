package com.incidentlocator.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.content.Context;
import android.location.LocationManager;

import android.location.Location;
import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
import android.util.Log;


public class IncidentLocator extends Activity
{
    private static final String TAG = "IncidentLocator";

    protected LocationManager locationManager;

    private static int lat;
    private static int lng;

    private TextView textView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView = (TextView) findViewById(R.id.show_message);

        locationManager =
            (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // TODO: request both gps and network updates
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0, new GetLocationListener());
    }

    public class GetLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            lat = (int) (loc.getLatitude()*1E6);
            lng = (int) (loc.getLongitude()*1E6);
            Log.d(TAG, "lat=" + lat);
            Log.d(TAG, "lng=" + lng);

            String message = Integer.toString(lat) + Integer.toString(lng);
            textView.setText(message);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }

    public void getLocation(View view) {
        String message = "Hello from incident Locator";

        TextView textView = (TextView) findViewById(R.id.show_message);
        textView.setText(message);
    }
}
