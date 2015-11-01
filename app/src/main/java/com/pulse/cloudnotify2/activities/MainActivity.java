package com.pulse.cloudnotify2.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pulse.cloudnotify2.R;
import com.pulse.cloudnotify2.data.ApplicationConstants;
import com.pulse.cloudnotify2.data.Utility;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RequestParams params = new RequestParams();
    GoogleCloudMessaging gcm;
    Context appContext;
    String regId="";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String REG_ID = "regId";
    public static final String EMAIL_ID = "eMailId";
    EditText emailaddrEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = getApplicationContext();
        emailaddrEditText = (EditText) findViewById(R.id.email_entry);

        progressDialog = new ProgressDialog(this);
        //Set Progress Dialog Text
        progressDialog.setMessage("Registering...");
        //Set Cancelable = false;
        progressDialog.setCancelable(false);

        InitialUserCheck();

        SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");

        //when email ID is set in sharedPref, user will be taken to the Home Screen

        if(!TextUtils.isEmpty(registrationId)) {
            Intent intent = new Intent(appContext, MessagesActivity.class);
            intent.putExtra("regId", registrationId);
            startActivity(intent);
            finish();
        }
    }

    //first run to check if device supports play services/else the app wont get pas here

    private boolean InitialUserCheck(){

        boolean isFirstRun = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if(isFirstRun){
           if(!checkPlayServices()){
               return true;
           }
        }
        return  false;
    }

    //When RegisterMe button is clicked.

    public  void RegisterUser(View view){

        InitialUserCheck();
        String userEmailAddr = emailaddrEditText.getText().toString();

        if((!TextUtils.isEmpty(userEmailAddr)) && (Utility.vaidateEmail(userEmailAddr))){

            //Check if Google Play Service is installed in Device
            //Play service is needed to handle GCM stuffs.
            if(checkPlayServices()){
                registerInBackground(userEmailAddr);
            }
        }
           //When email is invalid
        else {
            Toast.makeText(appContext, "Please enter a valid email address... ", Toast.LENGTH_LONG).show();
        }
    }

    //AsyncTsk to register Device in GCM Server

    private void registerInBackground(final String emailID){
        new AsyncTask<Void, Void, String>(){
            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                try {
                    if(gcm == null){
                        gcm = GoogleCloudMessaging.getInstance(appContext);
                    }

                    regId = gcm.register(ApplicationConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID : " + regId;
                } catch (IOException e){
                    msg = "Error :" + e.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if(!TextUtils.isEmpty(regId)){

                    //store RegID created by GCM in sharedPrefs
                    storeRegIdinSharedPref(appContext, regId, emailID);
                    Toast.makeText(appContext, "Registered with GCM successfully.nn" + msg, Toast.LENGTH_LONG)
                            .show();
                }
            }
        }.execute(null, null, null);
    }

    //Store RegID and email entered by user in SharedPref.
    private void storeRegIdinSharedPref(Context context, String regId, String emailID){

        SharedPreferences prefs = getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putString(EMAIL_ID, emailID);
        editor.apply();
        storeRegIdinServer();
    }

    private void storeRegIdinServer(){
        progressDialog.show();
        params.put("regId", regId);
        //Make RESTful webservice call using AsyncHTTPClient
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.GCM_SERVER_URL, params, new AsyncHttpResponseHandler() {
            //When the response returned by rest has HTTP error code 200
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressDialog.hide();

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(appContext, "RegID shared successfully with Web App", Toast.LENGTH_LONG).show();
                Intent i = new Intent(appContext, MessagesActivity.class);
                i.putExtra("regId", regId);
                startActivity(i);
                finish();
            }

            //when the response returned by REST has anything other than 200OK
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.hide();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                //when error is 404
                if (statusCode == 404) {
                    Toast.makeText(appContext, "Requested Resource not found", Toast.LENGTH_LONG).show();
                }
                //when HTTP is error 500
                else if (statusCode == 500) {
                    Toast.makeText(appContext, "Something went wrong on server side..", Toast.LENGTH_LONG).show();
                }
                //When response is anything other than 404/500
                else {
                    Toast.makeText(appContext, "Unexpected error has occurred..Check your connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //check if google play services is installed on the device..
    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //show error dialog to install play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();

            }
            else {
                Toast.makeText(appContext, "This device doesn't support play services, app will not work normally", Toast.LENGTH_LONG).show();
                return false;
                }
        }  else {
            Toast.makeText(appContext, "This device supports play, App will work normally", Toast.LENGTH_LONG).show();
        }
        return  true;
    }

}