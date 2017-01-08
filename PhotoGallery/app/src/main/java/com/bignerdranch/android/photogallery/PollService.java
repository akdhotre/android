package com.bignerdranch.android.photogallery;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by adhotre on 11/11/16.
 */
public class PollService extends IntentService {
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";
    private static String TAG = "PollService";

    private static long POLL_INTERVAL = AlarmManager.INTERVAL_HOUR;

    public static final String ACTION_SHOW_NOTIFICATION = "com.bignerdranch.android.photogallery.SHOW_NOTIFICATION";
    public static final String PERM_PRIVATE = "com.bignerdranch.android.photogallery.PRIVATE";

    public static Intent createIntent(Context context){
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Received an Intent: " + intent);

        if(!isNetworkAvailableAndConnected()){
            return;
        }

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;

        if(query == null){
            items = new FlickrFetch().fetchRecentPhotos();
        }else{
            items = new FlickrFetch().searchPhotos(query);
        }

        String resultId = items.get(0).getId();
        if(resultId.equals(lastResultId)){
            Log.d(TAG, "Got an old result:" + lastResultId);
        }else{
            Log.d(TAG, "Got an new result:" + resultId);

            Resources resources = getResources();
            Intent i = PhotoGalleryActivity.newIntent(this);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            Notification notification = new NotificationCompat.Builder(this).
                    setTicker(resources.getString(R.string.new_picture_title)).
                    setSmallIcon(android.R.drawable.ic_menu_report_image).
                    setContentTitle(resources.getString(R.string.new_picture_title)).
                    setContentText(resources.getString(R.string.new_picture_text)).
                    setContentIntent(pi).
                    setAutoCancel(true).
                    build();

            showBackgroundNotification(0, notification);
        }

        QueryPreferences.setLastResultId(this, resultId);

    }

    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isAvailable = cm.getActiveNetworkInfo().isAvailable();
        boolean isConnected = isAvailable && cm.getActiveNetworkInfo().isConnected();
        return isConnected;
    }

    public static void setAlarmService(Context context, boolean isOn){
        Intent i = PollService.createIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        if(isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pendingIntent);
        }else{
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        QueryPreferences.setAlarmOn(context, isOn);
    }

    public static boolean isServiceAlarmOn(Context context){
        Intent i = createIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    private void showBackgroundNotification(int requestCode, Notification notification){
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(i, PERM_PRIVATE, null, null, Activity.RESULT_OK, null, null);
    }
}
