package com.bignerdranch.android.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by adhotre on 11/11/16.
 */
public class QueryPreferences {

    private static final String PREF_SEARCH_QUERY = "searchQuery";

    private static final String PREF_LAST_RESULT_ID = "lastResultId";
    private static final String PREF_ALARM_ON = "isAlarmOn";

    public static String getStoredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);

    }

    public static void setStoredQuery(Context context, String query){
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().
                putString(PREF_SEARCH_QUERY, query).
                apply();
    }


    public static String getLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).
                getString(PREF_LAST_RESULT_ID, null);
    }

    public static void setLastResultId(Context context, String resultId){
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().
                putString(PREF_LAST_RESULT_ID, resultId).
                apply();
    }

    public static boolean isAlarmOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(PREF_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context).
                            edit().putBoolean(PREF_ALARM_ON, isOn).
                            apply();
    }
}
