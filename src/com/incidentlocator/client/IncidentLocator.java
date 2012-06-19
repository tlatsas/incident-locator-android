package com.incidentlocator.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.content.Context;
import android.location.LocationManager;
import com.incidentlocator.client.GetLocationListener;

public class IncidentLocator extends Activity
{

    private LocationManager locationManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        locationManager =
            (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // TODO: request both gps and network updates
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0, new GetLocationListener());
    }

    public void getLocation(View view) {
        String message = "Hello from incident Locator";

        TextView textView = (TextView) findViewById(R.id.show_message);
        textView.setText(message);
    }
}
