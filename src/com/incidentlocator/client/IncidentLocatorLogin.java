package com.incidentlocator.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import android.webkit.URLUtil;

import java.util.Map;
import java.util.HashMap;

import com.incidentlocator.client.HttpRest;

public class IncidentLocatorLogin extends Activity {
    private static final String TAG = "IncidentLocatorLogin";
    private static final String PREFS = "IncidentLocatorPreferences";
    private static Context context;
    private SharedPreferences settings;

    private EditText usernameTxt;
    private EditText passwordTxt;
    private EditText hostTxt;

    private HttpRest http = new HttpRest(IncidentLocatorLogin.this);

    private String username;
    private String password;
    private String host;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IncidentLocatorLogin.context = getApplicationContext();

        setContentView(R.layout.login);

        usernameTxt = (EditText)findViewById(R.id.txt_username);
        passwordTxt = (EditText)findViewById(R.id.txt_password);
        hostTxt = (EditText)findViewById(R.id.txt_host);

        // retrieve last used credentials from settings
        settings = context.getSharedPreferences(PREFS, 0);
        username = settings.getString("username", "");
        password = settings.getString("password", "");
        host = settings.getString("host", context.getString(R.string.default_server));

        usernameTxt.setText(username);
        passwordTxt.setText(password);
        hostTxt.setText(host);
    }

    public void httpLogin(View view) {
        Map<String, String> data = new HashMap<String, String>();

        username = usernameTxt.getText().toString().trim();
        password = passwordTxt.getText().toString().trim();
        host = hostTxt.getText().toString().trim();

        if (username.equals("") || password.equals("")) {
            Toast toast = Toast.makeText(context,
                "Email and password are required", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (URLUtil.isHttpUrl(host) || URLUtil.isHttpsUrl(host)) {
            // on valid data save on settings and call login task
            settings = context.getSharedPreferences(PREFS, 0);
            SharedPreferences.Editor editor = settings.edit();

            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("host", host);
            editor.commit();

            http.setHost(host);

            // set data for login
            data.put("email", username);
            data.put("password", password);
            http.login(data);
        } else {
            Toast toast = Toast.makeText(context,
                "Invalid host URL", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
