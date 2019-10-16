package com.example.journeytracking.TrackingModule;

import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import com.example.journeytracking.Data.RideDetails;
import com.example.journeytracking.Data.RideLocationUpdates;
import com.example.journeytracking.Utils.DbManager;
import com.example.journeytracking.Utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TrackingPresenter implements TrackingContract.Presenter {

    //instance of our activity
    private TrackingContract.View mView;
    private DbManager dbManager;
    //fields needed to set and update the timer for the ride
    private long startTime, timeInMilliseconds = 0;
    private Handler customHandler = new Handler();

    public TrackingPresenter(@NonNull TrackingContract.View view, DbManager dbManager) {
        this.mView = view;
        this.dbManager = dbManager;
    }

    //function to insert a new ride into the dp
    @Override
    public void insertNewRide(LatLng startLocation, boolean isRideComplete, String startTime) {
        RideDetails rideDetails = new RideDetails(startLocation.latitude, startLocation.longitude, isRideComplete, startTime);
        dbManager.insertRideDetails(rideDetails, new DbOperationCallbackImpl(mView));
    }

    //function to insert location updates for the current ride
    @Override
    public void insertLocationUpdate(Location currentLocation, long rideId) {
        RideLocationUpdates locationUpdates = new RideLocationUpdates(rideId, currentLocation.getLatitude(), currentLocation.getLongitude());
        dbManager.insertLocationUpdates(locationUpdates, new DbOperationCallbackImpl(mView));
    }

    //fucntion to update the ride when it has ended
    @Override
    public void updateCurrentRide(LatLng endLocation, double distanceCovered, boolean isRideComplete, String endTime,  long rideId) {
        RideDetails rideDetails = new RideDetails(endLocation.latitude, endLocation.longitude, distanceCovered, isRideComplete, endTime, rideId );
        dbManager.updateRideDetails(rideDetails, new DbOperationCallbackImpl(mView));
    }

    //function to start the timer to be displayed in our activity
    //feeds anew value to the activity every second till the ride ends
    @Override
    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    //remove remaining time update tasks if any
    @Override
    public void resetTimer() {
        customHandler.removeCallbacks(updateTimerThread);
        startTime = 0;
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            long newTime = SystemClock.uptimeMillis();
            timeInMilliseconds = newTime - startTime;
            mView.updateTimeElapsed(String.valueOf(Utils.getTimeFromMillis(timeInMilliseconds)));
            customHandler.postDelayed(this, 1000);
        }
    };

    //set the activity object to null if the activity has been destroyed
    @Override
    public void onDetach() {
        mView = null;
        customHandler.removeCallbacks(updateTimerThread);
        startTime = 0;
    }

    /*
    * Callback class for database operations
    * holds a weak reference to our activity to initiate callbacks
    *
    * */
    private static class DbOperationCallbackImpl implements DbManager.DbOperationCallback{
        //Weak reference to prevent memory leaks
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

        @Override
        public void onRideLocationsListFetched(ArrayList<RideLocationUpdates> rideLocationsList) {

        }
    }


}
