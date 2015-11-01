package com.pulse.cloudnotify2.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pulse.cloudnotify2.R;

public class MessagesActivity extends AppCompatActivity {

    TextView msgEditText, usertitleEditText;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        //Intent Message sent from broadcast Receiver.

        String str = getIntent().getStringExtra("message");

        //Get Email ID from Shared Preferences
        SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String emailID = prefs.getString("eMailId", "");
        //set title
        usertitleEditText = (TextView) findViewById(R.id.user_id);

        //check if play services is installed..

        if (checkPlayServices()) {
            Toast.makeText(getApplicationContext(), "This device does not support Google PLay Services", Toast.LENGTH_LONG).show();
        }

        usertitleEditText.setText("Hello, " + emailID + " !"); //TODO - remove concatenation in editText

        //When message sent from Broadcast Receiver is not empty.
        if (str != null) {
            //set the message
            msgEditText = (TextView) findViewById(R.id.msgview);
            msgEditText.setText(str);
        }
    }
        //Check if Google Play Services is installed on Device or not

    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //show error dialog to install play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "This device doesn't support play services, app will not work normally", Toast.LENGTH_SHORT).show();
                finish();
            }
        }  else {
            Toast.makeText(getApplicationContext(), "This device supports play, App will work normally", Toast.LENGTH_SHORT).show();
        }
            return  true;
        }
    //when application is resumed, check for play services

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
}
