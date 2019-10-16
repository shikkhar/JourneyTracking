package com.example.journeytracking.TrackingModule;

import android.location.Location;

import com.example.journeytracking.Utils.AppLocationManager;
import com.google.android.gms.maps.model.LatLng;

public interface TrackingContract {
    interface Presenter{
        void insertNewRide(LatLng startLocation, boolean isRideComplete, String startTime);
        void insertLocationUpdate(Location currentLocation, long rideId);
        void updateCurrentRide(LatLng endLocation, double distanceCovered, boolean isRideComplete,String endTime, long rideId);
        void startTimer();
        void resetTimer();
        void onDetach();
    }
    interface View{
        void onRideInserted(long insertedRowId);
        void onLocationUpdateInserted();
        void onRideUpdated();
        void updateTimeElapsed(String time);
    }
}
