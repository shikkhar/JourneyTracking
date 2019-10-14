package com.example.journeytracking.Utils;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

public class AppLocationManager {
    private FusedLocationProviderClient fusedLocationProviderClient;

    public AppLocationManager(Context context) {
        this.fusedLocationProviderClient = new FusedLocationProviderClient(context);
    }

    public void requestLocationUpdates(LocationRequest locationRequest, LocationCallback locationCallback, Looper looper){
        this.fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper);
    }

    public void removeLocationUpdates(LocationCallback locationCallback){
        this.fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /*public void getStartLocation(OnCompleteListener<Location> locationCallback){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(locationCallback);
    }*/
}
