package com.example.journeytracking.Utils;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;


/**
 * This class provides location updates based on the location request object
 * using the fused provider client
 */
public class AppLocationManager {
    private FusedLocationProviderClient fusedLocationProviderClient;

    public AppLocationManager(Context context) {
        this.fusedLocationProviderClient = new FusedLocationProviderClient(context);
    }

    //provides location updates to the callback object in the thread associated with the looper
    public void requestLocationUpdates(LocationRequest locationRequest, LocationCallback locationCallback, Looper looper){
        try {
            this.fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper);
        } catch(IllegalStateException e){
            e.printStackTrace();
        }
    }

    public void removeLocationUpdates(LocationCallback locationCallback){
        try {
            this.fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        } catch(IllegalStateException e){
            e.printStackTrace();
        }
    }

}
