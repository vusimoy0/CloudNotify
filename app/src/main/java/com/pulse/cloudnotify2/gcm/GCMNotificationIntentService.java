package com.pulse.cloudnotify2.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.NotificationManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pulse.cloudnotify2.R;
import com.pulse.cloudnotify2.activities.MessagesActivity;
import com.pulse.cloudnotify2.data.ApplicationConstants;

/**
 * Created by Vusi on 01 Nov 2015.
 *reference - http://goo.gl/SBB6qn
 */

//Handles the actual GCM message and posts the result to the user UI


public class GCMNotificationIntentService  extends IntentService{
    //Sets an ID for the notification so it can be updated.
    public static  final int notifyID = 9001;

    public GCMNotificationIntentService(){
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);

        String messageType = googleCloudMessaging.getMessageType(intent);

        if(!extras.isEmpty()){
            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){
                sendNotification("Send error: " + extras.toString());
            } else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType));
                sendNotification("GCM Server Message: \n" + extras.get(ApplicationConstants.MSG_KEY));
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg){
        Intent resultIntent = new Intent(this, MessagesActivity.class);
        resultIntent.putExtra("message", msg);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mNotifyBuilder;
        NotificationManager manager;

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyBuilder = new NotificationCompat.Builder (this)
                 .setContentTitle("CloudNotify")
                 .setContentText("A Notification Is Here")
                 .setSmallIcon(R.drawable.ic_launcher);

        //set pending intent.
        mNotifyBuilder.setContentIntent(resultPendingIntent);

        //set vibrate, sound and light
        int defaults = 0;

        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);

        //set content for the notification
        mNotifyBuilder.setContentText("New GCM Message");
        //set autocancel
        mNotifyBuilder.setAutoCancel(true);
        manager.notify(notifyID, mNotifyBuilder.build());

    }
}


