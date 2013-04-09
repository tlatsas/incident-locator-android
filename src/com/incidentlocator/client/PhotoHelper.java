package com.incidentlocator.client;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.MediaStore;
import android.os.Environment;
import android.util.Log;
import android.net.Uri;
import java.lang.AssertionError;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;

public final class PhotoHelper {
    private static final String TAG = "IncidentLocator";

    private PhotoHelper() {
        throw new AssertionError();
    }

    public static boolean isCameraAppAvailable(Context context) {
        final PackageManager pm = context.getPackageManager();
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list =
            pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static boolean hasCamera(Context context) {
        final PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /*
     * Check of SD is mounted and is writeable
     *
     */
    public static boolean isSdAvailable() {
        final String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static Uri getNewPhotoFileUri() {
        final File photoDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES);
        final File mediaStorageDir = new File(photoDir, TAG);

        if (! mediaStorageDir.exists()) {
            if (! mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create photo directory");
                return null;
            }
        }

        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_" + timeStamp + ".jpg");

        Log.d(TAG, "created file: "+mediaFile.getAbsolutePath());
        return Uri.fromFile(mediaFile);
    }
}
