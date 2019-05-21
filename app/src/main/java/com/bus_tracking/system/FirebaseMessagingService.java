package com.bus_tracking.system;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
   String TAG="FCM";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        sendNotification(remoteMessage.getData().get("body"));
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        JSONObject jsonObject;
        String msg=null,from=null,requester_token=null,status=null,title=null,latitude=null,longitude=null;
        try{ jsonObject=new JSONObject(messageBody);
//        from=jsonObject.getString("from");
            msg=jsonObject.getString("msg");
            title=jsonObject.getString("title");
//            requester_token=jsonObject.getString("token_id");
//            latitude=jsonObject.getString("latitude");
//            longitude=jsonObject.getString("longitude");
//            status=jsonObject.getString("status");
        }catch (Exception e){e.printStackTrace();}


        Log.i(TAG, "sendNotification: requester tokeinid"+requester_token);
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("msg",msg);
//        intent.putExtra("from",from);
//        intent.putExtra("longitude",longitude);
//        intent.putExtra("latitude",latitude);
//        intent.putExtra("status",status);
//        intent.putExtra("requester_token",requester_token);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)

                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
