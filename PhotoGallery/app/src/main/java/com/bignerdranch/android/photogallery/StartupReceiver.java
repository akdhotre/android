package com.bignerdranch.android.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by adhotre on 11/13/16.
 */
public class StartupReceiver extends BroadcastReceiver {
    private String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received broadcast intent: " + intent);

        boolean isOn = QueryPreferences.isAlarmOn(context);
        PollService.setAlarmService(context, isOn);

    }
}
