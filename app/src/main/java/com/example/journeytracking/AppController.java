package com.example.journeytracking;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.journeytracking.Data.RideDatabase;
import com.example.journeytracking.Utils.CONSTANTS;

/**
 * Subclass of our Application calss
 * Used to create the notification channel
 * and singleton instance of room db, volley request queue
 */
public class AppController extends Application {

    private RequestQueue mRequestQueue;
    private  RideDatabase db;
    private static AppController mInstance;
    public  final String DB_NAME = "rideDatabase";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        createNotificationChannel();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public synchronized RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            return mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        else
            return mRequestQueue;
    }

    public synchronized RideDatabase getRideDatabaseInstance(){
        if(db == null){
            db = Room.databaseBuilder(mInstance.getApplicationContext(), RideDatabase.class, DB_NAME).build();
            return db;
        }

        return db;
    }

    private void createNotificationChannel() {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CONSTANTS.Notification.CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }
}
