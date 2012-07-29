package com.incidentlocator.client;

import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;
import com.incidentlocator.client.IncidentLocatorInterface;

public class GetLocationListener implements LocationListener {
    private static final String TAG = "IncidentLocatorLocationListener";
    private static IncidentLocatorInterface app;

    // constructor - get interface from main activity
    public GetLocationListener (IncidentLocatorInterface i) {
        app = i;
    }

    @Override
    public void onLocationChanged(Location location) {
        app.updateLocation(location);
        Log.d(TAG, String.format("[%s]=>%s", location.getProvider(), location.toString()));
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}

