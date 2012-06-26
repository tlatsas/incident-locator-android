/*
 * LocationLogger.java
 *
 * Simple class to record obtained locations in external
 * storage (usually SD card) for debugging/testing purposes
 *
 */
package com.incidentlocator.client;

import android.location.Location;
import android.content.Context;
import android.util.Log;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class LocationLogger {

    private static final String TAG = "IncidentLocatorExternalLogger";
    private static final String extFile = "locations.log";

    public void saveLocation(Location loc, Context context) {
        File extDir = context.getExternalFilesDir(null);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File fp = new File(extDir, extFile);
                // open file for append
                FileWriter fw = new FileWriter(fp, true);
                BufferedWriter f = new BufferedWriter(fw);
                f.write(loc.toString()+"\n");
                f.close();
                Log.d(TAG, "wrote to external log");
            } catch (IOException e) {
                Log.w(TAG, e);
            }
        } else {
            Log.w(TAG, "external storage not mounted");
        }
    }
}
