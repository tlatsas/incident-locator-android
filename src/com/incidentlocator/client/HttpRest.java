package com.incidentlocator.client;

import android.util.Log;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.widget.Toast;

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
import java.lang.CharSequence;

import org.json.JSONObject;
import org.json.JSONException;

public class HttpRest {
    private static final String TAG = "IncidentLocator::HttpRest";
    private static final String PREFS = "IncidentLocatorPreferences";

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

    public void profile() {
        new RestProfile().execute();
    }

    public void report(Map data) {
        new RestReport().execute(data);
    }

    // -----------------------------------------------------------------------
    // helper methods
    // -----------------------------------------------------------------------

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

    protected void setLogin(boolean status) {
        SharedPreferences settings = context.getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("logged_in", status);
        editor.commit();
    }

    // -----------------------------------------------------------------------
    // http async tasks
    // -----------------------------------------------------------------------

    private class RestLogin extends AsyncTask <Map, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Sign in...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Map... data) {
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
                Log.d(TAG, str_response);
                return false;
            }

            try {
                msg = json_response.getString("msg");
            } catch (JSONException e) {
                Log.d(TAG, "could not get 'msg' from response");
                Log.d(TAG, str_response);
                return false;
            }

            Log.d(TAG, msg);
            return true;
        }

        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            setLogin(result.booleanValue());

            if (result == true) {
                Log.d(TAG, "change view");
            } else {
                int duration = Toast.LENGTH_SHORT;
                CharSequence text = "Cannot login to service";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }

    private class RestProfile extends AsyncTask <Void, Void, String> {

        ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Fetching used data...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // default return value on errors
            String str_response = new String("{}");

            // hold response from api here
            String msg = new String("");

            JSONObject json_response = null;
            InputStream response = null;

            try {
                URL url = new URL(host + "api/profile");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");

                response = new BufferedInputStream(urlConnection.getInputStream());
                str_response = readStream(response);
                json_response = new JSONObject(str_response);

            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
                return str_response;
            } finally {
                try {
                    if (response != null) {
                        response.close();
                    }
                } catch (IOException logOrIgnore) {
                    Log.d(TAG, "io error");
                }
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

    private class RestReport extends AsyncTask <Map, Void, String> {

        ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Sending report...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
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

                URL url = new URL(host + "api/report");
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

}
