package com.example.journeytracking.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
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


public class TrackingService extends Service {
    private static final String TAG = "TrackingService";
    public static final int NOTIFICATION_ID = 123;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10 * 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private LocalBinder binder = new LocalBinder();
    private LocationRequest locationRequest;
    private CurrentLocationCallback locationCallback;
    private AppLocationManager appLocationManager;
    private SharedPrefManager sharedPrefManager;
    private NewLocationCallback newLocationCallback;

    public TrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createLocationRequest();
        locationCallback = new CurrentLocationCallback();
        appLocationManager = new AppLocationManager(this);
        sharedPrefManager = new SharedPrefManager(getApplicationContext());

    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        appLocationManager.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        //TODO: check if this is really needed
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (sharedPrefManager.isRideStarted())
            startForeground(NOTIFICATION_ID, getNotification());
        else
            stopSelf();
        return true;
    }


    public void requestLocationUpdates(NewLocationCallback newLocationCallback) {
        this.newLocationCallback = newLocationCallback;
        Intent serviceIntent = new Intent(getApplicationContext(), TrackingService.class);
        startService(serviceIntent);
    }

    public void removeLocationUpdates() {
        appLocationManager.removeLocationUpdates(locationCallback);
        stopSelf();

    }

    public class LocalBinder extends Binder {
        public TrackingService getService() {
            return TrackingService.this;
        }
    }

    private class CurrentLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onNewLocationReceived(locationResult.getLastLocation());
        }


    }


    private void onNewLocationReceived(Location location) {

        newLocationCallback.onNewLocationReceived(location);

    }


    private Notification getNotification() {


        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, TrackingActivity.class), 0);

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


    /*public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }*/

    public interface NewLocationCallback{
        void onNewLocationReceived(Location location);
    }
}
