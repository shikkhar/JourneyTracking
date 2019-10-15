package com.example.journeytracking.TrackingModule;


import android.location.Location;

import androidx.annotation.NonNull;

import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;
import com.example.journeytracking.Utils.DbManager;
import com.google.android.gms.maps.model.LatLng;


import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TrackingPresenter implements TrackingContract.Presenter {

    private TrackingContract.View mView;
    private DbManager dbManager;

    public TrackingPresenter(@NonNull TrackingContract.View view, DbManager dbManager) {
        this.mView = view;
        this.dbManager = dbManager;
    }

    @Override
    public void insertNewRide(LatLng startLocation, boolean isRideComplete, String startTime) {
        RideDetails rideDetails = new RideDetails(startLocation.latitude, startLocation.longitude, isRideComplete, startTime);
        dbManager.insertRideDetails(rideDetails, new DbOperationCallbackImpl(mView));
    }

    @Override
    public void insertLocationUpdate(Location currentLocation, long rideId) {
        RideLocationUpdates locationUpdates = new RideLocationUpdates(rideId, currentLocation.getLatitude(), currentLocation.getLongitude());
        dbManager.insertLocationUpdates(locationUpdates, new DbOperationCallbackImpl(mView));
    }

    @Override
    public void updateCurrentRide(LatLng endLocation, double distanceCovered, boolean isRideComplete, String endTime,  long rideId) {
        RideDetails rideDetails = new RideDetails(endLocation.latitude, endLocation.longitude, distanceCovered, isRideComplete, endTime, rideId );
        dbManager.updateRideDetails(rideDetails, new DbOperationCallbackImpl(mView));
    }

    private static class DbOperationCallbackImpl implements DbManager.DbOperationCallback{
        private WeakReference<TrackingContract.View> mView;

        public DbOperationCallbackImpl(TrackingContract.View view) {
            this.mView = new WeakReference<>(view);
        }

        @Override
        public void onInsertRide(long insertedRowId) {
            TrackingContract.View view = mView.get();
            if(view != null){
                view.onRideInserted(insertedRowId);
            }
        }

        @Override
        public void onInsertLocationUpdate(long insertedRowId) {
            TrackingContract.View view = mView.get();
            if(view != null){
               view.onLocationUpdateInserted();
            }
        }

        @Override
        public void onUpdateRide() {
            TrackingContract.View view = mView.get();
            if(view != null){
                view.onRideUpdated();
            }
        }

        @Override
        public void onRideListFetched(ArrayList<RideDetails> rideDetailsList) {

        }
    }

    /* @Override
    public void getStartLocation(AppLocationManager locationManager) {
        locationManager.getStartLocation(new StartLocationFetchedCallback(mView));
    }

    private static class StartLocationFetchedCallback implements OnCompleteListener<Location> {
        private static final String TAG = "StartLocationFetchedCal";

        private WeakReference<TrackingContract.View> mView;

        public StartLocationFetchedCallback(TrackingContract.View view) {
            this.mView = new WeakReference<>(view);
        }

        @Override
        public void onComplete(@NonNull Task<Location> task) {

            boolean success = task.isSuccessful();
            Location location = task.getResult();
            if (task.isSuccessful() && task.getResult() != null) {
                TrackingContract.View view = mView.get();
                if (view != null)
                    view.setStartLocation(task.getResult());
            } else {

                Log.d(TAG, "Failed to get location.");
            }

        }
    }*/
}
