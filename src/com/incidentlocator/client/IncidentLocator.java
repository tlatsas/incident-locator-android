package com.incidentlocator.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;

import com.incidentlocator.client.GetLocationListener;
import com.incidentlocator.client.GetDirectionListener;
import com.incidentlocator.client.IncidentLocatorInterface;

import android.provider.Settings;
import java.text.DecimalFormat;

import com.incidentlocator.client.LocationLogger;
import android.text.format.Time;
import android.util.Log;

import android.os.AsyncTask;
import java.util.Map;
import java.util.HashMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONObject;


public class IncidentLocator extends Activity implements IncidentLocatorInterface {
    private static final String TAG = "IncidentLocator";
    private static Context context;

    private LocationManager locationManager;
    private LocationListener locationListener = new GetLocationListener(this);

    private SensorManager sensorManager;
    private SensorEventListener sensorListener = new GetDirectionListener(this);
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;

    // user location object
    private Location location;
    // device heading in degrees
    private int heading;

    private EditText coordinatesBox;
    private TextView headingView;
    private LocationLogger locLogger = new LocationLogger();

    // -----------------------------------------------------------------------
    // implement interface methods
    // -----------------------------------------------------------------------

    public void updateLocation(Location loc) {
        location = loc;
    }

    public void updateDirection(int heading) {
        this.heading = heading;
    }

    // -----------------------------------------------------------------------
    // activity life-cycle methods
    // -----------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IncidentLocator.context = getApplicationContext();
        setContentView(R.layout.main);

        coordinatesBox = (EditText) findViewById(R.id.show_message);
        headingView = (TextView) findViewById(R.id.show_heading);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // init sensor manager and sensors types
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        // separate older entries in external log file using dates
        // and a separator
        Time now = new Time();
        now.setToNow();
        String sep = String.format("==[ %s ] =======================", now.toString());
        locLogger.saveLocation(sep, IncidentLocator.context);

        Map<String, String> data = new HashMap<String, String>();
        data.put("email", "");
        data.put("password", "");

        new RestLogin().execute(data);
    }

    @Override
    public void onStart() {
        super.onStart();

        // check if GPS is enabled
        boolean isGpsEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (! isGpsEnabled) {
            promptOpenLocationSettings("GPS is disabled. Do you want to enable it?");
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // register device listeners
        sensorManager.registerListener(sensorListener, accelerometerSensor,
                                       SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(sensorListener, magnetometerSensor,
                                       SensorManager.SENSOR_DELAY_UI);
   }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
        sensorManager.unregisterListener(sensorListener);
    }

    // -----------------------------------------------------------------------
    // interface controls methods
    // -----------------------------------------------------------------------

    public void getLocation(View view) {
        StringBuilder sb = new StringBuilder(512);
        if (location == null) {
            sb.append("waiting location data...\n");
        } else {
            DecimalFormat df = new DecimalFormat(".000000");
            df.setRoundingMode(java.math.RoundingMode.DOWN);
            sb
                .append("Lat: ")
                .append(df.format(location.getLatitude()))
                .append("Lng: ")
                .append(df.format(location.getLongitude()))
                .append("\n");

            // log to external storage for debugging/testing
            locLogger.saveLocation(location.toString(), IncidentLocator.context);
        }
        coordinatesBox.append(sb.toString());
        headingView.setText(String.valueOf(heading));
    }

    // -----------------------------------------------------------------------
    // helper methods
    // -----------------------------------------------------------------------

    private void promptOpenLocationSettings(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
            .setMessage(message)
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

    // -----------------------------------------------------------------------
    // HTTP REST methods
    // -----------------------------------------------------------------------
    private class RestLogin extends AsyncTask <Map, Void, String> {
        @Override
        protected String doInBackground(Map... data) {
            try {
                JSONObject json_data = new JSONObject(data[0]);
                byte[] byte_data = json_data.toString().getBytes("UTF-8");

                URL url = null;
                HttpURLConnection urlConnection = null;

                url = new URL("http://10.0.2.2:3000/api/signin");

                urlConnection = (HttpURLConnection) url.openConnection();

                OutputStream output = null;
                try {
                    // make a POST request
                    urlConnection.setDoOutput(true);

                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setFixedLengthStreamingMode(byte_data.length);

                    output = urlConnection.getOutputStream();
                    output.write(byte_data);
                    output.flush();
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException logOrIgnore) {
                            Log.d(TAG, "HTTP Login Failed");
                        }
                    }
                }
                InputStream response = urlConnection.getInputStream();
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return "test";
        }
    }
    /*
    private class RestProfile extends AsyncTask <Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
        }
    }

    private class RestReport extends AsyncTask <Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
        }
    }*/

}
