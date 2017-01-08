package com.bignerdranch.android.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by adhotre on 11/13/16.
 */
public class NotificationReceiver extends BroadcastReceiver{
    private static String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent:" + intent);

        if(getResultCode() != Activity.RESULT_OK){
            Log.i(TAG, "Request code:" + getResultCode());
            return;
        }

        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
        Notification notification = (Notification) intent.getParcelableExtra(PollService.NOTIFICATION);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(requestCode, notification);

    }
}
