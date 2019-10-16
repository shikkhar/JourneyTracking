package com.example.journeytracking.RideHistoryModule.RideDetailsModule;

import com.example.journeytracking.Data.RideLocationUpdates;

import java.util.ArrayList;

public interface RideDetailContract {
    interface Presenter{
        void getRideLocationList(Long rideId);
        void snapToRoad(ArrayList<RideLocationUpdates> rideLocationUpdatesList, String apiKey);
        void onDetach();
    }
    interface View{
        void onRideLocationListFetched(ArrayList<RideLocationUpdates> rideLocationUpdatesList);
        void snappedPointsBuildResult(ArrayList<RideLocationUpdates> snappedPointsList);
        void snappedPointsFinalResult(ArrayList<RideLocationUpdates> snappedPointsList);
    }
}
