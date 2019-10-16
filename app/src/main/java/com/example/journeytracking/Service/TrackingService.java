package com.example.journeytracking.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import com.example.journeytracking.R;
import com.example.journeytracking.TrackingModule.TrackingActivity;
import com.example.journeytracking.Utils.AppLocationManager;
import com.example.journeytracking.Utils.CONSTANTS;
import com.example.journeytracking.Utils.SharedPrefManager;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 *
 *
 * This app use sa long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification associated with that service is removed.
 */
public class TrackingService extends Service {
    private static final String TAG = "TrackingService";
    //The identifier for the notification displayed for the foreground service.
    public static final int NOTIFICATION_ID = 123;
    //The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5 * 1000;
    //The fastest rate for active location updates. Updates will never be more frequent
    //  than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    //Binder object to provide instance of this service to bound components
    private LocalBinder binder = new LocalBinder();
    //Location updates will be provided based on this request
    private LocationRequest locationRequest;
    //callback for location updates
    private CurrentLocationCallback locationCallback;
    //this class provides the location updates via FusedLocationProviderClient
    private AppLocationManager appLocationManager;
    private SharedPrefManager sharedPrefManager;
    private TrackingActivityCallback trackingActivityCallback;
    private GpsLocationReceiver gpsLocationReceiver;

    public TrackingService() {
    }

    /*//Broadcast receiver for Location Settings Changes
    * checks for permissions every time a location setting change takes place by initiating a callback to the activity
    * if the location is turned off user is requested to turn it on again
    * */
    public class GpsLocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                if(trackingActivityCallback != null)
                    trackingActivityCallback.checkGpsSettings();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createLocationRequest();
        //callback object for location updates
        locationCallback = new CurrentLocationCallback();
        //regular location updates are provided by this class
        appLocationManager = new AppLocationManager(this);
        sharedPrefManager = new SharedPrefManager(getApplicationContext());
        gpsLocationReceiver = new GpsLocationReceiver();

        //Broadcast receiver is registered here and unregistered in onDestroy()
        //It is tied to the lifecycle of the service because if the service is running it means that the user wants location
        // updates from the service, therefore it's necessary to have appropriate location settings
        registerReceiver(gpsLocationReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }

    //create the request based on which location updates will be provided
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        //minimum interval between two requests..this is not fixed
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        //fastest time where we can get location computed by other components
        //should be less tha setInterval
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        //since it's a tracking app, we need precise data for our calculations
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //if the activity is binding to the service, it means that the app is not longer in the background
    //therefore remove the foreground notification
    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        return binder;
    }

    //if the activity is binding to the service, it means that the app is not longer in the background
    //therefore remove the foreground notification
    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        appLocationManager.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        return START_STICKY;
    }

    //if the activity is binding to the service, it means that the app is not longer in the background
    //if we have a ride in progress, start a foreground service, or else kill the service if no ride is in progress
    @Override
    public boolean onUnbind(Intent intent) {
        if (sharedPrefManager.isRideStarted())
            startForeground(NOTIFICATION_ID, getNotification());
        else
            stopSelf();
        return true;
    }

    //function to start receiving location updates
    public void requestLocationUpdates(TrackingActivityCallback trackingActivityCallback) {
        this.trackingActivityCallback = trackingActivityCallback;
        //if the service was only created but not started, start it
        Intent serviceIntent = new Intent(getApplicationContext(), TrackingService.class);
        startService(serviceIntent);
    }

    //function to stop receiving location updates
    //since we don't need updates stop the service
    public void removeLocationUpdates() {
        this.trackingActivityCallback = null;
        appLocationManager.removeLocationUpdates(locationCallback);
        stopSelf();
    }

    //unregister the broadcast receiver
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(gpsLocationReceiver);
    }

    public class LocalBinder extends Binder {
        public TrackingService getService() {
            return TrackingService.this;
        }
    }

    /*
    Callback class for new location updates
    This in r=turn makes a callback to the activity so that it can work with the new locations
     */
    private class CurrentLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onNewLocationReceived(locationResult.getLastLocation());
        }
    }

    private void onNewLocationReceived(Location location) {
        trackingActivityCallback.onNewLocationReceived(location);
    }


    //Notification builder for the foreground notifications
    private Notification getNotification() {
        // The PendingIntent to launch activity.

        Intent intent =  new Intent(this, TrackingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CONSTANTS.Notification.CHANNEL_ID)
                .setContentText(getString(R.string.notification_text))
                .setContentTitle(getString(R.string.app_name))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(activityPendingIntent);
        return builder.build();
    }


    //Callback Interface implemented by the activity
    //used when a new location is received or location settings are changed
    public interface TrackingActivityCallback {
        void onNewLocationReceived(Location location);
        void checkGpsSettings();
    }


}
