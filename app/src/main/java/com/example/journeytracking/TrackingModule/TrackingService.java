package com.example.journeytracking.TrackingModule;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.journeytracking.Utils.AppLocationManager;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class TrackingService extends Service {
    private static final String TAG = "TrackingService";

    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice";
    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    static final String CURRENT_LOCATION = PACKAGE_NAME + ".current_location";
    static final String END_LOCATION = PACKAGE_NAME + ".end_location";

   // private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
    //        ".started_from_notification";


    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10*1000;

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private LocalBinder binder = new LocalBinder();
    private LocationRequest locationRequest;
    private CurrentLocationCallback locationCallback;
    private AppLocationManager appLocationManager;
    private Handler serviceHandler;

    public TrackingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createLocationRequest();
        locationCallback = new CurrentLocationCallback();
        appLocationManager = new AppLocationManager(this);
/*
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());*/
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
       return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    void requestLocationUpdates(){
        Intent serviceIntent = new Intent(getApplicationContext(), TrackingService.class);
        startService(serviceIntent);
    }

    void removeLocationUpdates(){
        appLocationManager.removeLocationUpdates(locationCallback);
        stopSelf();

    }

    public class LocalBinder extends Binder{
        TrackingService getService(){
            return TrackingService.this;
        }
    }

    private class CurrentLocationCallback extends LocationCallback{
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onNewLocationReceived(locationResult.getLastLocation());
        }


    }


    private void onNewLocationReceived(Location location) {

        if(!serviceIsRunningInForeground(TrackingService.this)) {
            Intent intent = new Intent(ACTION_BROADCAST);
            intent.putExtra(CURRENT_LOCATION, location);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }


    public boolean serviceIsRunningInForeground(Context context) {
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
    }
}
