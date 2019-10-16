package com.example.journeytracking.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class to manage shared preferences of our app
 */
public class SharedPrefManager {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "myPrefs";
    private static final String KEY_RIDE_STARTED = "user_id";


    public SharedPrefManager(Context appContext) {
        mSharedPreferences = appContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mSharedPreferences.edit();

    }

    //update preference to notify if a ride is in progress
    public void setRideStarted(boolean state){
        mEditor.putBoolean(KEY_RIDE_STARTED, state);
        mEditor.commit();
    }

    //preference to check whether  a ride is in progress or not
    public boolean isRideStarted(){
        return mSharedPreferences.getBoolean(KEY_RIDE_STARTED, false);
    }
}
