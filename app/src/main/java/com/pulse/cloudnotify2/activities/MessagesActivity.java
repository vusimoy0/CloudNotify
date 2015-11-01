package com.pulse.cloudnotify2.activities;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.pulse.cloudnotify2.R;

public class MessagesActivity extends AppCompatActivity {

    TextView msgEditText, usertitleEditText;


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

        usertitleEditText.setText("Hello, " + emailID + " !"); //TODO - remove concatenation in editText

        //When message sent from Broadcast Receiver is not empty.
        if (str != null) {
            //set the message
            msgEditText = (TextView) findViewById(R.id.msgview);
            msgEditText.setText(str);
        }
    }
}
