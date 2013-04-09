package com.incidentlocator.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.provider.Settings;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.net.Uri;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.HashMap;

import com.incidentlocator.client.GetLocationListener;
import com.incidentlocator.client.GetDirectionListener;
import com.incidentlocator.client.IncidentLocatorInterface;
import com.incidentlocator.client.HttpRest;
import com.incidentlocator.client.IncidentLocatorLogin;
import com.incidentlocator.client.LocationLogger;
import com.incidentlocator.client.PhotoHelper;

public class IncidentLocator extends Activity implements IncidentLocatorInterface {
    private static final String TAG = "IncidentLocator";
    private static final String PREFS = "IncidentLocatorPreferences";
    private static final int PHOTO_CODE = 100;
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
    private Uri imageUri;

    private EditText coordinatesBox;
    private TextView headingView;
    private LocationLogger locLogger = new LocationLogger();

    private HttpRest http = new HttpRest(IncidentLocator.this);

    private SharedPreferences settings;

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

        settings = getSharedPreferences(PREFS, 0);

        boolean logged_in = settings.getBoolean("logged_in", false);
        if (logged_in == false) {
            Log.d(TAG, "starting login service");

            Intent login = new Intent(this, IncidentLocatorLogin.class);
            startActivity(login);

            // do not proceed to onStart()
            finish();
        }

        setContentView(R.layout.main);

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
    }

    @Override
    public void onStart() {
        super.onStart();

        coordinatesBox = (EditText) findViewById(R.id.show_message);
        headingView = (TextView) findViewById(R.id.show_heading);
        http.profile();

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
    // interface callback methods
    // -----------------------------------------------------------------------

    public void getLocation(View view) {
        if (hasLocation()) {
            logLocation();
        } else {
            coordinatesBox.append("Location data not available yet.. \n");
        }
    }

    public void sendReport(View view) {
        if (hasLocation()) {
            logLocation();
            Map data = reportData();
            http.report(data);
        } else {
            CharSequence text = "Location is unavailable";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void takePhoto(View view) {
        if (PhotoHelper.hasCamera(context)) {
            if (PhotoHelper.isCameraAppAvailable(context)) {
                if (PhotoHelper.isSdAvailable()) {
                    dispatchTakePhotoIntent();
                } else {
                    CharSequence text = "Cannot write to external medium";
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                }
            } else {
                CharSequence text = "No suitable camera application found installed";
                Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            CharSequence text = "Your device does not support this feature";
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_CODE) {
            String msg;
            if (resultCode == RESULT_OK) {
                msg = "Photo saved successfully";
                dispatchMediaScanIntent();
            } else if (resultCode == RESULT_CANCELED) {
                msg = "Photo activity canceled";
            } else {
                msg = "Failed taking photo";
            }
            Log.d(TAG, msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    // -----------------------------------------------------------------------
    // helper methods
    // -----------------------------------------------------------------------

    private void dispatchTakePhotoIntent() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = PhotoHelper.getNewPhotoFileUri();
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(takePhotoIntent, PHOTO_CODE);
    }

    private void dispatchMediaScanIntent() {
        Intent scan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scan.setData(imageUri);
        Log.d(TAG, "sending media scan broadcast");
        sendBroadcast(scan);
    }

    private Map reportData() {
        Map<String, Double> data = new HashMap<String, Double>();
        data.put("latitude", location.getLatitude());
        data.put("longitude", location.getLongitude());
        data.put("heading", (double)heading);
        return data;
    }

    private boolean hasLocation() {
        return (location == null)? false : true;
    }

    /* log location/heading on application log and on the coordinates box */
    private void logLocation() {
        DecimalFormat df = new DecimalFormat(".000000");
        df.setRoundingMode(java.math.RoundingMode.DOWN);

        StringBuilder sb = new StringBuilder(512);
        sb
            .append("Lat: ")
            .append(df.format(location.getLatitude()))
            .append("Lng: ")
            .append(df.format(location.getLongitude()))
            .append("\n");

        coordinatesBox.append(sb.toString());
        headingView.setText(String.valueOf(heading));

        // application logs
        locLogger.saveLocation(location.toString(), IncidentLocator.context);
    }

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
}
