package com.incidentlocator.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Context;
import android.widget.EditText;
import android.util.Log;

import java.util.Map;
import java.util.HashMap;

import com.incidentlocator.client.HttpRest;

public class IncidentLocatorLogin extends Activity {
    private static final String TAG = "IncidentLocatorLogin";
    private static Context context;

    private EditText usernameTxt;
    private EditText passwordTxt;

    private HttpRest http = new HttpRest(IncidentLocatorLogin.this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IncidentLocatorLogin.context = getApplicationContext();

        setContentView(R.layout.login);

        usernameTxt = (EditText)findViewById(R.id.txt_username);
        passwordTxt = (EditText)findViewById(R.id.txt_password);
    }

    public void httpLogin(View view) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("email", usernameTxt.getText().toString());
        data.put("password", passwordTxt.getText().toString());

        // TODO: get this from an optional text field
        http.setHost("http://10.0.2.2:3000/");

        http.login(data);
    }
}
