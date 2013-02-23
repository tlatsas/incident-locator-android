package com.incidentlocator.client;

import android.util.Log;
import android.content.Context;
import android.os.AsyncTask;
import android.app.ProgressDialog;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONException;

public class HttpRest {
    private static final String TAG = "IncidentLocator::HttpRest";

    private String host = "http://10.0.2.2:3000/";
    private Context context = null;

    public HttpRest(Context c) {
        context = c;
    }

    public void setHost(String h) {
        host = h;
    }

    public void login(Map data) {
        new RestLogin().execute(data);
    }

    protected String readStream(InputStream is) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                out.write(i);
                i = is.read();
            }
            return out.toString();
        } catch (IOException e) {
            return "{}";
        }
    }

    //public void login() {
    //    new RestProfile().execute(data);
    //}

    //public void login(Map data) {
    //    new RestReport().execute(data);
    //}

    // -----------------------------------------------------------------------
    // http async tasks
    // -----------------------------------------------------------------------

    private class RestLogin extends AsyncTask <Map, Void, String> {

        ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Sign in...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Map... data) {
            // default return value on errors
            String str_response = new String("{}");

            // hold response from api here
            String msg = new String("");

            JSONObject json_response = null;

            try {
                JSONObject json_data = new JSONObject(data[0]);
                byte[] byte_data = json_data.toString().getBytes("UTF-8");

                URL url = new URL(host + "api/signin");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

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
                            Log.d(TAG, "io error");
                        }
                    }
                }

                InputStream response = new BufferedInputStream(urlConnection.getInputStream());
                str_response = readStream(response);
                json_response = new JSONObject(str_response);

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
                return str_response;
            }

            try {
                msg = json_response.getString("msg");
            } catch (JSONException e) {
                Log.d(TAG, "could not get 'msg' from response");
                return str_response;
            }

            Log.d(TAG, msg);
            return msg;
        }

        protected void onPostExecute(String result) {
            dialog.dismiss();
        }
    }

    //private class RestProfile extends AsyncTask <Map, Void, String> { }
    //private class RestReport extends AsyncTask <Map, Void, String> { }
}
